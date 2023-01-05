package com.joker.mybatis.builder.xml;

import com.joker.mybatis.builder.BaseBuilder;
import com.joker.mybatis.builder.MapperBuilderAssistant;
import com.joker.mybatis.io.Resources;
import com.joker.mybatis.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 解耦映射解析
 * Mapper XML 配置构建器，主要负责解析 Mapper 映射 Statement 配置文件。
 * 即 <select />、<insert />、<update />、<delete /> 标签。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/20
 */
public class XMLMapperBuilder extends BaseBuilder {

    private Element element;
    // 映射器构建助手
    private MapperBuilderAssistant builderAssistant;
    private String resource;

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource) throws DocumentException {
        this(new SAXReader().read(inputStream), configuration, resource);
    }

    private XMLMapperBuilder(Document document, Configuration configuration, String resource) {
        super(configuration);
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
        this.element = document.getRootElement();
        this.resource = resource;
    }

    /**
     * 解析
     */
    public void parse() throws Exception {
        // 如果当前资源没有加载过再加载，防止重复加载
        if (!configuration.isResourceLoaded(resource)) {
            configurationElement(element);
            // 标记一下，已经加载过了
            configuration.addLoadedResource(resource);
            // 绑定映射器到namespace Mybatis 源码方法名 -> bindMapperForNamespace
            configuration.addMapper(Resources.classForName(builderAssistant.getCurrentNamespace()));

        }
    }

    /**
     * 配置mapper元素
     * <mapper namespace="org.mybatis.example.BlogMapper">
     * <select id="selectBlog" parameterType="int" resultType="Blog">
     * select * from Blog where id = #{id}
     * </select>
     * </mapper>
     *
     * @param element
     */
    private void configurationElement(Element element) {
        // 1.配置 namespace
        String namespace = element.attributeValue("namespace");
        if (namespace.equals("")) {
            throw new RuntimeException("Mapper's namespace cannot be empty");
        }
        builderAssistant.setCurrentNamespace(namespace);

        // 2.配置 select|insert|update|delete
        buildStatementFromContext(element.elements("select"),
                element.elements("insert"),
                element.elements("update"),
                element.elements("delete"));
    }

    /**
     * <p>
     * 配置select|insert|update|delete
     * </P>
     *
     * @param lists
     * @author jokerzzccc
     * @date 2022/11/24
     */
    private void buildStatementFromContext(List<Element>... lists) {
        for (List<Element> list : lists) {
            for (Element element : list) {
                final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, element);
                statementParser.parseStatementNode();
            }
        }
    }

}
