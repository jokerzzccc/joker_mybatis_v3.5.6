package com.joker.mybatis.scripting.xmltags;

/**
 * <p>
 * <if /> 标签的 SqlNode 实现类:
 * 主要是验证标签表达式是否满足要求。而这个判断操作就是使用OGNL表达式进行处理的。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/8
 */
public class IfSqlNode implements SqlNode {

    /**
     * ognl 表达式求值器
     */
    private final ExpressionEvaluator evaluator;
    /**
     * 判断表达式
     */
    private final String test;
    /**
     * 内嵌的 SqlNode 节点
     */
    private final SqlNode contents;

    public IfSqlNode(SqlNode contents, String test) {
        this.test = test;
        this.contents = contents;
        this.evaluator = new ExpressionEvaluator();
    }

    @Override
    public boolean apply(DynamicContext context) {
        // 如果满足条件，则apply，并返回true
        if (evaluator.evaluateBoolean(test, context.getBindings())) {
            contents.apply(context);
            return true;
        }
        return false;
    }

}
