package io.maling.pojo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 存放全局 Config 配置文件中的数据
 * @author maling
 */
public class Configuration {

    // 保持配置文件中设置的数据源
    private DataSource dataSource;

    /**
     * 当前数据源对应的所有 Sql 信息
     * key: Statement Id
     * value: 封装好的 MappedStatement 对象
     */
    private Map<String, MappedStatement> mappedStatementMap = new HashMap<>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MappedStatement> getMappedStatementMap() {
        return mappedStatementMap;
    }

    public void setMappedStatementMap(Map<String, MappedStatement> mappedStatementMap) {
        this.mappedStatementMap = mappedStatementMap;
    }

}
