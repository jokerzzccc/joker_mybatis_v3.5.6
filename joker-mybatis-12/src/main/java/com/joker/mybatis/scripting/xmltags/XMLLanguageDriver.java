package com.joker.mybatis.scripting.xmltags;

import com.joker.mybatis.executor.parameter.ParameterHandler;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.mapping.SqlSource;
import com.joker.mybatis.scripting.LanguageDriver;
import com.joker.mybatis.scripting.defaults.DefaultParameterHandler;
import com.joker.mybatis.scripting.defaults.RawSqlSource;
import com.joker.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * <p>
 * XML语言驱动器
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/24
 */
public class XMLLanguageDriver implements LanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType) {
        // 用XML脚本构建器解析
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
        return builder.parseScriptNode();

    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        // 暂时不解析动态 SQL
        return new RawSqlSource(configuration, script, parameterType);

    }

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

}
