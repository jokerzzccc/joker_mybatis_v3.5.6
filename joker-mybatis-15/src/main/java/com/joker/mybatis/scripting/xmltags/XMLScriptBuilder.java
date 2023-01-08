package com.joker.mybatis.scripting.xmltags;

import com.joker.mybatis.builder.BaseBuilder;
import com.joker.mybatis.mapping.SqlSource;
import com.joker.mybatis.scripting.defaults.RawSqlSource;
import com.joker.mybatis.session.Configuration;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * XML脚本构建器:
 * 继承 BaseBuilder 抽象类，XML 动态语句( SQL )构建器，负责将 SQL 解析成 SqlSource 对象。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/24
 */
public class XMLScriptBuilder extends BaseBuilder {

    /**
     * 当前 SQL 的 element 对象
     */
    private Element element;
    /**
     * 是否为动态 SQL
     */
    private boolean isDynamic;
    /**
     * SQL 方法类型
     */
    private Class<?> parameterType;
    /**
     * NodeHandler 实现类的缓存
     * key: 动态标签名
     * value: 对应的 NodeHandler 实现类
     */
    private final Map<String, NodeHandler> nodeHandlerMap = new HashMap<>();

    public XMLScriptBuilder(Configuration configuration, Element element, Class<?> parameterType) {
        super(configuration);
        this.element = element;
        this.parameterType = parameterType;
        // 初始化 nodeHandlerMap 属性
        initNodeHandlerMap();
    }

    private void initNodeHandlerMap() {
        // 9种， trim/where/set/foreach/if/choose/when/otherwise/bind
        // 本次实现其中2种: trim/if
        nodeHandlerMap.put("trim", new TrimHandler());
        nodeHandlerMap.put("if", new IfHandler());
    }

    /**
     * 负责将 SQL 解析成 SqlSource 对象
     *
     * @return
     */
    public SqlSource parseScriptNode() {
        List<SqlNode> contents = parseDynamicTags(element);
        MixedSqlNode rootSqlNode = new MixedSqlNode(contents);
        SqlSource sqlSource;
        if (isDynamic) {
            sqlSource = new DynamicSqlSource(configuration, rootSqlNode);
        } else {
            sqlSource = new RawSqlSource(configuration, rootSqlNode, parameterType);
        }
        return sqlSource;
    }

    /**
     * 解析 SQL 成 SqlNode 对象
     *
     * @param element
     * @return
     */
    protected List<SqlNode> parseDynamicTags(Element element) {
        List<SqlNode> contents = new ArrayList<>();
        // 遍历 SQL 节点的所有子节点
        List<Node> children = element.content();
        for (Node child : children) {
            if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
                String data = child.getText();
                TextSqlNode textSqlNode = new TextSqlNode(data);
                if (textSqlNode.isDynamic()) {
                    contents.add(textSqlNode);
                    isDynamic = true;
                } else {
                    contents.add(new StaticTextSqlNode(data));
                }
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getName();
                NodeHandler handler = nodeHandlerMap.get(nodeName);
                if (handler == null) {
                    throw new RuntimeException("Unknown element <" + nodeName + "> in SQL statement.");
                }
                handler.handleNode(element.element(child.getName()), contents);
                isDynamic = true;
            }
        }
        return contents;
    }

    /**
     * 节点处理器接口：将 element 节点创建成对象的 SqlNode 对象。
     * 每个 MyBatis 的自定义的 XML 标签对应专属的一个 NodeHandler 实现类。
     */
    private interface NodeHandler {

        /**
         * 处理 Node
         *
         * @param nodeToHandle 要处理的 element 节点
         * @param targetContents 目标的 SqlNode 数组。实际上，被处理的 element 节点会创建成对应的 SqlNode 对象，添加到 targetContents 中
         */
        void handleNode(Element nodeToHandle, List<SqlNode> targetContents);

    }

    /**
     * <trim /> 标签的处理器。
     */
    private class TrimHandler implements NodeHandler {

        @Override
        public void handleNode(Element nodeToHandle, List<SqlNode> targetContents) {
            List<SqlNode> contents = parseDynamicTags(nodeToHandle);
            MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
            String prefix = nodeToHandle.attributeValue("prefix");
            String prefixOverrides = nodeToHandle.attributeValue("prefixOverrides");
            String suffix = nodeToHandle.attributeValue("suffix");
            String suffixOverrides = nodeToHandle.attributeValue("suffixOverrides");
            TrimSqlNode trim = new TrimSqlNode(configuration, mixedSqlNode, prefix, prefixOverrides, suffix, suffixOverrides);
            targetContents.add(trim);
        }

    }

    /**
     * <if /> 标签的处理器。
     */
    private class IfHandler implements NodeHandler {

        @Override
        public void handleNode(Element nodeToHandle, List<SqlNode> targetContents) {
            List<SqlNode> contents = parseDynamicTags(nodeToHandle);
            MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
            String test = nodeToHandle.attributeValue("test");
            IfSqlNode ifSqlNode = new IfSqlNode(mixedSqlNode, test);
            targetContents.add(ifSqlNode);
        }

    }

}
