package io.maling.conf;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import io.maling.io.Resources;
import io.maling.pojo.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XmlConfigBuilder {

    private Configuration configuration;
    public XmlConfigBuilder() {
        this.configuration = new Configuration();

    }

    /**
     * 将 xml 解析为 Configuration
     * @param in 输入流
     * @return 配置对象
     */
    public Configuration parseConfig(InputStream in) throws DocumentException, PropertyVetoException {
        Document document = new SAXReader().read(in);
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.selectNodes("//property");
        Properties properties = new Properties();
        for (Element element : list) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name, value);
        }
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(properties.getProperty("driverClass"));
        dataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        dataSource.setUser(properties.getProperty("username"));
        dataSource.setPassword(properties.getProperty("password"));
        dataSource.setInitialPoolSize(3);
        dataSource.setMaxIdleTime(60);
        dataSource.setMaxPoolSize(20);
        dataSource.setMinPoolSize(5);
        configuration.setDataSource(dataSource);

        // mapper.xml 加载
        // 1. 从 mapper 标签中拿到路径
        list = rootElement.selectNodes("//mapper");
        for (Element element : list) {
            String xmlPath = element.attributeValue("resource");
            // 2. 拿到 mapper xml 输入流
            InputStream resourceAsStream = Resources.getResourceAsStream(xmlPath);
            // 3. 解析 mapper xml，最麻烦
            XmlMapperBuilder xmlMapperBuilder = new XmlMapperBuilder(configuration);
            xmlMapperBuilder.parse(resourceAsStream);
        }

        return this.configuration;
    }

}
