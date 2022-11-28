package com.joker.mybatis.scripting.xmltags;

import com.joker.mybatis.mapping.SqlSource;
import com.joker.mybatis.scripting.LanguageDriver;
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

}
