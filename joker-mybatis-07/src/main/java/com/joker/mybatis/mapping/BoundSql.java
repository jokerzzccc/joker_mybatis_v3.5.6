package com.joker.mybatis.mapping;

import java.util.Map;

/**
 * <p>
 * An actual SQL String got from an {@link SqlSource} after having processed any dynamic content.
 * The SQL may have SQL placeholders "?" and a list (ordered) of a parameter mappings
 * with the additional information for each parameter (at least the property name of the input object to read
 * the value from).
 * <p>
 * Can also have additional parameters that are created by the dynamic language (for loops, bind...).
 * </p>
 * 绑定的 SQL,是从 SqlSource 而来，将动态内容都处理完成得到的 SQL 语句字符串，其中包括 ?,还有绑定的参数
 *
 * @author jokerzzccc
 * @date 2022/10/18
 */
public class BoundSql {

    private String sql;

    private Map<Integer, String> parameterMappings;

    private String parameterType;

    private String resultType;

    public BoundSql(String sql, Map<Integer, String> parameterMappings, String parameterType, String resultType) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterType = parameterType;
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public Map<Integer, String> getParameterMappings() {
        return parameterMappings;
    }

    public String getParameterType() {
        return parameterType;
    }

    public String getResultType() {
        return resultType;
    }

}
