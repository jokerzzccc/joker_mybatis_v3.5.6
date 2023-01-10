package com.joker.mybatis.scripting;

import com.joker.mybatis.executor.parameter.ParameterHandler;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.mapping.SqlSource;
import com.joker.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * <p>
 * 脚本语言驱动接口：
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/21
 */
public interface LanguageDriver {

    /**
     * <p>
     * Creates an {@link SqlSource} that will hold the statement read from a mapper xml file.
     * It is called during startup, when the mapped statement is read from a class or an xml file.
     * <p>
     * 从 Mapper XML 配置的 Statement 标签中，创建 SqlSource 对象，即 <select /> 等。
     * </P>
     *
     * @param configuration The MyBatis configuration
     * @param script XNode parsed from a XML file
     * @param parameterType input parameter type got from a mapper method or specified in the parameterType xml attribute. Can be null.
     * @return com.joker.mybatis.mapping.SqlSource
     * @author jokerzzccc
     * @date 2022/11/23
     */
    SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType);

    /**
     * Creates an {@link SqlSource} that will hold the statement read from an annotation.
     * It is called during startup, when the mapped statement is read from a class or an xml file.
     * <p>
     * 创建SQL源码(annotation 注解方式)
     *
     * @param configuration The MyBatis configuration
     * @param script The content of the annotation
     * @param parameterType input parameter type got from a mapper method or specified in the parameterType xml attribute. Can be null.
     * @return the sql source
     */
    SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType);

    /**
     * 创建参数处理器
     */
    ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);

}
