package com.joker.mybatis.mapping;

import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.type.JdbcType;
import com.joker.mybatis.type.TypeHandler;

/**
 * <p>
 * 结果映射
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/12/28
 */
public class ResultMapping {

    private Configuration configuration;
    private String property;
    private String column;
    private Class<?> javaType;
    private JdbcType jdbcType;
    private TypeHandler<?> typeHandler;

    ResultMapping() {
    }

    public static class Builder {
        private ResultMapping resultMapping = new ResultMapping();
    }

}
