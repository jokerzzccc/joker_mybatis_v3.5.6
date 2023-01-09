package com.joker.mybatis.scripting.xmltags;

import com.joker.mybatis.parsing.GenericTokenParser;
import com.joker.mybatis.parsing.TokenHandler;
import com.joker.mybatis.type.SimpleTypeRegistry;

import java.util.regex.Pattern;

/**
 * <p>
 * 文本SQL节点(CDATA|TEXT): 文本的 SqlNode 实现类。
 * 相比 StaticTextSqlNode 的实现来说，TextSqlNode 不确定是否为静态文本，所以提供 #isDynamic() 方法，进行判断是否为动态文本。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/8
 */
public class TextSqlNode implements SqlNode {

    /**
     * 文本
     */
    private final String text;
    /**
     * 目前该属性只在单元测试中使用，暂时无视
     */
    private final Pattern injectionFilter;

    public TextSqlNode(String text) {
        this(text, null);
    }

    public TextSqlNode(String text, Pattern injectionFilter) {
        this.text = text;
        this.injectionFilter = injectionFilter;
    }

    public boolean isDynamic() {
        DynamicCheckerTokenParser checker = new DynamicCheckerTokenParser();
        GenericTokenParser parser = createParser(checker);
        parser.parse(text);
        return checker.isDynamic();
    }


    @Override
    public boolean apply(DynamicContext context) {
        // <1> 创建 BindingTokenParser 对象
        // <2> 创建 GenericTokenParser 对象
        GenericTokenParser parser = createParser(new BindingTokenParser(context, injectionFilter));
        // <3> 执行解析
        // <4> 将解析的结果，添加到 context 中
        context.appendSql(parser.parse(text));
        return true;
    }

    private GenericTokenParser createParser(TokenHandler handler) {
        return new GenericTokenParser("${", "}", handler);
    }


    /**
     * 绑定记号解析器
     */
    private static class BindingTokenParser implements TokenHandler {

        private DynamicContext context;
        private Pattern injectionFilter;

        public BindingTokenParser(DynamicContext context, Pattern injectionFilter) {
            this.context = context;
            this.injectionFilter = injectionFilter;
        }

        @Override
        public String handleToken(String content) {
            // 初始化 value 属性到 context 中
            Object parameter = context.getBindings().get("_parameter");
            if (parameter == null) {
                context.getBindings().put("value", null);
            } else if (SimpleTypeRegistry.isSimpleType(parameter.getClass())) {
                context.getBindings().put("value", parameter);
            }
            // 使用 OGNL 表达式，获得对应的值
            Object value = OgnlCache.getValue(content, context.getBindings());
            String srtValue = (value == null ? "" : String.valueOf(value));
            checkInjection(srtValue);
            // 返回该值
            return srtValue;
        }

        /**
         * 检查是否匹配正则表达式
         */
        private void checkInjection(String value) {
            if (injectionFilter != null && !injectionFilter.matcher(value).matches()) {
                throw new RuntimeException("Invalid input. Please conform to regex" + injectionFilter.pattern());
            }
        }

    }
    /**
     * 动态SQL检查器
     */
    private static class DynamicCheckerTokenParser implements TokenHandler {

        private boolean isDynamic;

        public DynamicCheckerTokenParser() {
            // Prevent Synthetic Access
        }

        public boolean isDynamic() {
            return isDynamic;
        }


        @Override
        public String handleToken(String content) {
            // 设置 isDynamic 为 true，即调用了这个类就必定是动态 SQL
            this.isDynamic = true;
            return null;

        }
    }

}
