package com.joker.mybatis.builder.xml;

import com.joker.mybatis.builder.BaseBuilder;
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
    private String resource;
    private String currentNamespace;

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource) throws DocumentException {
        this(new SAXReader().read(inputStream), configuration, resource);
    }

    private XMLMapperBuilder(Document document, Configuration configuration, String resource) {
        super(configuration);
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
            // 绑定映射器到namespace
            configuration.addMapper(Resources.classForName(currentNamespace));
        }
    }


    /**
     *  配置mapper元素
     * <mapper namespace="org.mybatis.example.BlogMapper">
     *   <select id="selectBlog" parameterType="int" resultType="Blog">
     *    select * from Blog where id = #{id}
     *   </select>
     * </mapper>
     * @param element
     */
    private void configurationElement(Element element) {
        // 1.配置 namespace
        currentNamespace = element.attributeValue("namespace");
        if (currentNamespace.equals("")) {
            throw new RuntimeException("Mapper's namespace cannot be empty");
        }

        // 2.配置 select|insert|update|delete
        buildStatementFromContext(element.elements("select"));
    }


    /**
     * <p>
     * 配置select|insert|update|delete
     * </P>
     *
     * @param list
     * @author jokerzzccc
     * @date 2022/11/24
     */
    private void buildStatementFromContext(List<Element> list){
        for (Element element : list) {
            final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, element, currentNamespace);
            statementParser.parseStatementNode();
        }
    }

}
