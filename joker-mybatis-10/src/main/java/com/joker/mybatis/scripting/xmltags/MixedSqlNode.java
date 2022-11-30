package com.joker.mybatis.scripting.xmltags;

import java.util.List;

/**
 * <p>
 * 混合SQL节点:混合的 SqlNode 实现类
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/24
 */
public class MixedSqlNode implements SqlNode {

    //组合模式，拥有一个SqlNode的List
    private List<SqlNode> contents;

    public MixedSqlNode(List<SqlNode> contents) {
        this.contents = contents;
    }

    @Override
    public boolean apply(DynamicContext context) {
        // 依次调用list里每个元素的apply
        contents.forEach(node -> node.apply(context));
        return true;
    }


}
