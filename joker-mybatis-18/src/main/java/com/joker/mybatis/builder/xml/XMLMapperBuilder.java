package com.joker.mybatis.builder.xml;

import com.joker.mybatis.builder.BaseBuilder;
import com.joker.mybatis.builder.MapperBuilderAssistant;
import com.joker.mybatis.builder.ResultMapResolver;
import com.joker.mybatis.cache.Cache;
import com.joker.mybatis.io.Resources;
import com.joker.mybatis.mapping.ResultFlag;
import com.joker.mybatis.mapping.ResultMap;
import com.joker.mybatis.mapping.ResultMapping;
import com.joker.mybatis.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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
        // 1. 配置 namespace
        String namespace = element.attributeValue("namespace");
        if (namespace.equals("")) {
            throw new RuntimeException("Mapper's namespace cannot be empty");
        }
        builderAssistant.setCurrentNamespace(namespace);

        // 2. 配置cache
        cacheElement(element.element("cache"));

        // 3. 解析 resultMap
        resultMapElements(element.elements("resultMap"));

        // 4. 配置 select|insert|update|delete
        buildStatementFromContext(element.elements("select"),
                element.elements("insert"),
                element.elements("update"),
                element.elements("delete"));
    }

    /**
     * <cache eviction="FIFO" flushInterval="600000" size="512" readOnly="true"/>
     */
    private void cacheElement(Element context) {
        if (context == null) return;
        // 基础配置信息
        String type = context.attributeValue("type", "PERPETUAL");
        Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);
        // 缓存队列 FIFO
        String eviction = context.attributeValue("eviction", "FIFO");
        Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);
        Long flushInterval = Long.valueOf(context.attributeValue("flushInterval"));
        Integer size = Integer.valueOf(context.attributeValue("size"));
        boolean readWrite = !Boolean.parseBoolean(context.attributeValue("readOnly", "false"));
        boolean blocking = !Boolean.parseBoolean(context.attributeValue("blocking", "false"));

        // 解析额外属性信息；<property name="cacheFile" value="/tmp/xxx-cache.tmp"/>
        List<Element> elements = context.elements();
        Properties props = new Properties();
        for (Element element : elements) {
            props.setProperty(element.attributeValue("name"), element.attributeValue("value"));
        }
        // 构建缓存
        builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
    }

    private void resultMapElements(List<Element> list) {
        for (Element element : list) {
            try {
                resultMapElement(element, Collections.emptyList());
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * <p>
     * 解析 ResultMap 标签
     * </P>
     * 比如：
     * <resultMap id="activityMap" type="cn.bugstack.mybatis.test.po.Activity">
     * <id column="id" property="id"/>
     * <result column="activity_id" property="activityId"/>
     * <result column="activity_name" property="activityName"/>
     * <result column="activity_desc" property="activityDesc"/>
     * <result column="create_time" property="createTime"/>
     * <result column="update_time" property="updateTime"/>
     * </resultMap>
     *
     * @param resultMapNode
     * @param additionalResultMappings
     * @author jokerzzccc
     * @date 2023/1/6
     */
    private ResultMap resultMapElement(Element resultMapNode, List<ResultMapping> additionalResultMappings) throws Exception {
        String id = resultMapNode.attributeValue("id");
        String type = resultMapNode.attributeValue("type");
        Class<?> typeClass = resolveClass(type);

        List<ResultMapping> resultMappings = new ArrayList<>();
        resultMappings.addAll(additionalResultMappings);

        List<Element> resultChildren = resultMapNode.elements();
        for (Element resultChild : resultChildren) {
            List<ResultFlag> flags = new ArrayList<>();
            if ("id".equals(resultChild.getName())) {
                flags.add(ResultFlag.ID);
            }
            // 构建 ResultMapping
            resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
        }

        // 创建结果集映射器
        ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, resultMappings);
        return resultMapResolver.resolve();
    }

    /**
     * 将当前节点构建成 ResultMapping 对象;
     * <p>
     * 比如：
     * <id column="id" property="id"/>
     * <result column="activity_id" property="activityId"/>
     *
     * @param context resultMap 子节点
     * @param resultType
     * @param flags
     * @return
     */
    private ResultMapping buildResultMappingFromContext(Element context, Class<?> resultType, List<ResultFlag> flags) {
        String property = context.attributeValue("property");
        String column = context.attributeValue("column");
        return builderAssistant.buildResultMapping(resultType, property, column, flags);
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
