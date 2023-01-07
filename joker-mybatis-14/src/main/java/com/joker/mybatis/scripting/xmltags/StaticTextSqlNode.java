package com.joker.mybatis.scripting.xmltags;

/**
 * <p>
 * 静态文本SQL节点：静态文本的 SqlNode 实现类
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/24
 */
public class StaticTextSqlNode implements SqlNode {

    /**
     * 静态文本
     */
    private final String text;

    public StaticTextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(DynamicContext context) {
        // 直接拼接到 context 中
        context.appendSql(text);
        return true;
    }

}
