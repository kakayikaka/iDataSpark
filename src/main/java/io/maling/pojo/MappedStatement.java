package io.maling.pojo;

/**
 * 存放 Sql Config 配置文件中的数据
 * @author maling
 */
public class MappedStatement {
    // ID 标识
    private String id;

    // 参数类型
    private String resultType;

    // 返回值类型
    private String parameterType;

    // SQL 语句
    private String sql;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

}
