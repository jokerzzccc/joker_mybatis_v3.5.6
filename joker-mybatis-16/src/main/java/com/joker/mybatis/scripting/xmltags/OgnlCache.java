package com.joker.mybatis.scripting.xmltags;

import ognl.Ognl;
import ognl.OgnlException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * OGNL 缓存
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/8
 */
public class OgnlCache {

    /**
     * 表达式的缓存的映射
     * <p>
     * KEY：表达式
     * VALUE：表达式的缓存 @see #parseExpression(String)
     */
    private static final Map<String, Object> expressionCache = new ConcurrentHashMap<>();

    private OgnlCache() {
        // Prevent Instantiation of Static Class
    }

    /**
     * 获得表达式对应的值。
     *
     * @param expression
     * @param root
     * @return
     */
    public static Object getValue(String expression, Object root) {
        try {
            Map<Object, OgnlClassResolver> context = Ognl.createDefaultContext(root, new OgnlClassResolver());
            return Ognl.getValue(parseExpression(expression), context, root);
        } catch (OgnlException e) {
            throw new RuntimeException("Error evaluating expression '" + expression + "'. Cause: " + e, e);
        }
    }

    /**
     * 解析表达式, 获得表达式对应的值
     *
     * @param expression
     * @return
     * @throws OgnlException
     */
    private static Object parseExpression(String expression) throws OgnlException {
        Object node = expressionCache.get(expression);
        if (node == null) {
            // OgnlParser.topLevelExpression 操作耗时，加个缓存放到 ConcurrentHashMap 里面
            node = Ognl.parseExpression(expression);
            expressionCache.put(expression, node);
        }
        return node;
    }

}
