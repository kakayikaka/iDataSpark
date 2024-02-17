package io.maling.sqlSession;

import io.maling.pojo.Configuration;

import java.beans.IntrospectionException;
import java.lang.reflect.*;
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

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(),
                new Class[]{mapperClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 根据不同情况，调用不同的 SqlSession API，selectList、selectOne 等
                        /*
                         * 参数1: statementId：
                         *  sql 唯一 ID，代理无法直接拿到，
                         *  所以需要约定 DAO 接口路径等于 namespace，
                         *  DAO 接口中的方法名等于 sql id
                         * */
                        String sqlId = method.getName();
                        String namespace = method.getDeclaringClass().getName();
                        String statementId = namespace + "." + sqlId;
                        /*
                         * 参数2: params: 也就是 args
                         */
                        // 然后根据返回值类型，选择调用的 SqlSession 方法
                        Type genericReturnType = method.getGenericReturnType();
                        // 判断是否进行过泛型类型参数化
                        if (genericReturnType instanceof ParameterizedType) {
                            // 如果是，则表明是个集合
                            return selectList(statementId, args);
                        } else {
                            return selectOne(statementId, args);
                        }
                        // 这里只演示查询方法，所以判断比较简单
                    }
                });

        return (T) proxyInstance;
    }
}
