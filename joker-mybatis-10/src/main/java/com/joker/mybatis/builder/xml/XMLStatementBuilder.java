package com.joker.mybatis.builder.xml;

import com.joker.mybatis.builder.BaseBuilder;
import com.joker.mybatis.builder.MapperBuilderAssistant;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.mapping.SqlCommandType;
import com.joker.mybatis.mapping.SqlSource;
import com.joker.mybatis.scripting.LanguageDriver;
import com.joker.mybatis.session.Configuration;
import org.dom4j.Element;

import java.util.Locale;

/**
 * <p>
 * Statement XML 配置构建器，主要负责解析 Statement 配置，即 <select />、<insert />、<update />、<delete /> 标签。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/20
 */
public class XMLStatementBuilder extends BaseBuilder {

    private MapperBuilderAssistant builderAssistant;
    private Element element;

    public XMLStatementBuilder(Configuration configuration, MapperBuilderAssistant builderAssistant, Element element) {
        super(configuration);
        this.builderAssistant = builderAssistant;
        this.element = element;
    }

    /**
     * <p>
     * 解析语句(select|insert|update|delete)
     * 例如：
     * //<select
     * //  id="selectPerson"
     * //  parameterType="int"
     * //  parameterMap="deprecated"
     * //  resultType="hashmap"
     * //  resultMap="personResultMap"
     * //  flushCache="false"
     * //  useCache="true"
     * //  timeout="10000"
     * //  fetchSize="256"
     * //  statementType="PREPARED"
     * //  resultSetType="FORWARD_ONLY">
     * //  SELECT * FROM PERSON WHERE ID = #{id}
     * //</select>
     * </P>
     *
     * @author jokerzzccc
     * @date 2022/11/20
     */
    public void parseStatementNode() {
        String id = element.attributeValue("id");
        // 参数类型
        String parameterType = element.attributeValue("parameterType");
        Class<?> parameterTypeClass = resolveAlias(parameterType);
        // 外部应用 resultMap
        String resultMap = element.attributeValue("resultMap");
        // 结果类型
        String resultType = element.attributeValue("resultType");
        Class<?> resultTypeClass = resolveAlias(resultType);
        // 获取命令类型(select|insert|update|delete)
        String nodeName = element.getName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));

        // 获取默认语言驱动器
        Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
        LanguageDriver langDriver = configuration.getLanguageRegistry().getDriver(langClass);

        // 解析成SqlSource，DynamicSqlSource/RawSqlSource
        SqlSource sqlSource = langDriver.createSqlSource(configuration, element, parameterTypeClass);

        // 调用映射构建助手类【便于统一处理参数的包装】
        builderAssistant.addMappedStatement(id,
                sqlSource,
                sqlCommandType,
                parameterTypeClass,
                resultMap,
                resultTypeClass,
                langDriver);

    }

}
