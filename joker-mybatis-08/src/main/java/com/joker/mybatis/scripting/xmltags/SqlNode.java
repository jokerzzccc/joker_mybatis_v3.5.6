package com.joker.mybatis.scripting.xmltags;

/**
 * <p>
 * SQL 节点:每个 XML Node 会解析成对应的 SQL Node 对象。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/24
 */
public interface SqlNode {

    /**
     * 应用当前 SQL Node 节点
     *
     * @param context 上下文
     * @return 当前 SQL Node 节点是否应用成功。
     */
    boolean apply(DynamicContext context);

}
