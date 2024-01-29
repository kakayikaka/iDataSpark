package io.maling.sqlSession;

import io.maling.conf.XmlConfigBuilder;
import io.maling.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream in) throws PropertyVetoException, DocumentException {
        // 1. 通过 jom4j 解析配置文件，将解析出来的内容封装到 Configuration 中
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(in);
        // 2. 创建 SqlSessionFactory 对象
        return new DefaultSqlSessionFactory(configuration);
    }
}
