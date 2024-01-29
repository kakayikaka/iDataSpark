package io.maling.sqlSession;

import io.maling.conf.BoundSql;
import io.maling.pojo.Configuration;
import io.maling.pojo.MappedStatement;
import io.maling.utils.GenericTokenParser;
import io.maling.utils.ParameterMapping;
import io.maling.utils.ParameterMappingTokenHandler;

import javax.sql.DataSource;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor{
    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IntrospectionException, InstantiationException, InvocationTargetException {
        // 1. 注册驱动，获取连接
        DataSource dataSource = configuration.getDataSource();
        Connection connection = dataSource.getConnection();

        // 2. 获取 SQL 语句
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        // 3. 获取 jdbc PreparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        // 4. 设置参数
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterClass = getClassType(parameterType);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String content = parameterMapping.getContent();
            // 反射获取
            Field contentField = parameterClass.getDeclaredField(content);
            contentField.setAccessible(true);
            Object o = contentField.get(params[0]);
            preparedStatement.setObject(i+1, o);
        }

        // 5. 执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
        // 6. 封装结果集
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);
        List<Object> objects = new ArrayList<>();
        while (resultSet.next()) {
            Object instance = resultTypeClass.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(columnName);
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(instance, value);
            }
            objects.add(instance);
        }

        return (List<E>) objects;
    }

    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if (parameterType != null) {
            return Class.forName(parameterType);
        } else {
            return null;
        }
    }

    /**
     * 将 #{} 替换为 ?，解析出 #{} 中的值，并存储
     * @param sql sql
     * @return BoundSql
     */
    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", tokenHandler);
        String parseSql = genericTokenParser.parse(sql);
        List<ParameterMapping> parameterMappings = tokenHandler.getParameterMappings();
        return new BoundSql(parseSql, parameterMappings);
    }


}
