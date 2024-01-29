package io.maling.sqlSession;

import io.maling.pojo.Configuration;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession{

    private Configuration configuration;
    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws SQLException, IntrospectionException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Executor executor = new SimpleExecutor();
        List<Object> query = executor.query(configuration,
                configuration.getMappedStatementMap().get(statementId),
                params);
        return (List<E>) query;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws SQLException, IntrospectionException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        List<Object> objects = selectList(statementId, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else if (objects.isEmpty()){
            throw new RuntimeException("查询结果为空");
        } else {
            throw new RuntimeException("返回结果过多");
        }
    }
}
