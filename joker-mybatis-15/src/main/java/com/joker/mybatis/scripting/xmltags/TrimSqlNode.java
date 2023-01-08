package com.joker.mybatis.scripting.xmltags;

import com.joker.mybatis.session.Configuration;

import java.util.*;

/**
 * <p>
 * trim SqlNode 节点解析: <trim /> 标签的 SqlNode 实现类。
 * TrimSqlNode节点的解析，主要依赖于FilteredDynamicContext对配置信息的拼装，
 * 把AND|OR等，拼装到SQL语句上进行返回处理。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/8
 */
public class TrimSqlNode implements SqlNode {

    /**
     * 内含的 SqlNode 节点
     */
    private final SqlNode contents;
    /**
     * 前缀
     */
    private final String prefix;
    /**
     * 后缀
     */
    private final String suffix;
    /**
     * 需要被删除的前缀
     */
    private final List<String> prefixesToOverride;
    /**
     * 需要被删除的后缀
     */
    private final List<String> suffixesToOverride;
    private final Configuration configuration;

    public TrimSqlNode(Configuration configuration, SqlNode contents, String prefix, String prefixesToOverride, String suffix, String suffixesToOverride) {
        this(configuration, contents, prefix, parseOverrides(prefixesToOverride), suffix, parseOverrides(suffixesToOverride));
    }

    protected TrimSqlNode(Configuration configuration, SqlNode contents, String prefix, List<String> prefixesToOverride, String suffix, List<String> suffixesToOverride) {
        this.contents = contents;
        this.prefix = prefix;
        this.prefixesToOverride = prefixesToOverride;
        this.suffix = suffix;
        this.suffixesToOverride = suffixesToOverride;
        this.configuration = configuration;
    }

    @Override
    public boolean apply(DynamicContext context) {
        FilteredDynamicContext filteredDynamicContext = new FilteredDynamicContext(context);
        boolean result = contents.apply(filteredDynamicContext);
        filteredDynamicContext.applyAll();
        return result;
    }

    /**
     * 使用 | 分隔字符串成字符串数组，并都转换成大写。
     *
     * @param overrides
     * @return
     */
    private static List<String> parseOverrides(String overrides) {
        if (overrides != null) {
            final StringTokenizer parser = new StringTokenizer(overrides, "|", false);
            final List<String> list = new ArrayList<>(parser.countTokens());
            while (parser.hasMoreTokens()) {
                list.add(parser.nextToken().toUpperCase(Locale.ENGLISH));
            }
            return list;
        }
        return Collections.emptyList();
    }

    /**
     * 是 TrimSqlNode 的内部类，继承 DynamicContext 类: 支持 trim 逻辑的 DynamicContext 实现类。
     */
    private class FilteredDynamicContext extends DynamicContext {

        /**
         * 委托的 DynamicContext 对象
         */
        private DynamicContext delegate;
        /**
         * 是否 prefix 已经被应用
         */
        private boolean prefixApplied;
        /**
         * 是否 suffix 已经被应用
         */
        private boolean suffixApplied;
        /**
         * StringBuilder 对象
         *
         * @see #appendSql(String)
         */
        private StringBuilder sqlBuffer;

        public FilteredDynamicContext(DynamicContext delegate) {
            super(configuration, null);
            this.delegate = delegate;
            this.prefixApplied = false;
            this.suffixApplied = false;
            this.sqlBuffer = new StringBuilder();
        }

        public void applyAll() {
            // <1> trim 掉多余的空格，生成新的 sqlBuffer 对象
            sqlBuffer = new StringBuilder(sqlBuffer.toString().trim());
            // <2> 将 sqlBuffer 大写，生成新的 trimmedUppercaseSql 对象
            String trimmedUppercaseSql = sqlBuffer.toString().toUpperCase(Locale.ENGLISH);
            // <3> 应用 TrimSqlNode 的 trim 逻辑
            if (trimmedUppercaseSql.length() > 0) {
                applyPrefix(sqlBuffer, trimmedUppercaseSql);
                applySuffix(sqlBuffer, trimmedUppercaseSql);
            }
            // <4> 将结果，添加到 delegate 中
            delegate.appendSql(sqlBuffer.toString());
        }

        @Override
        public Map<String, Object> getBindings() {
            return delegate.getBindings();
        }

        @Override
        public void bind(String name, Object value) {
            delegate.bind(name, value);
        }

        @Override
        public int getUniqueNumber() {
            return delegate.getUniqueNumber();
        }

        @Override
        public void appendSql(String sql) {
            sqlBuffer.append(sql);
        }

        @Override
        public String getSql() {
            return delegate.getSql();
        }

        private void applyPrefix(StringBuilder sql, String trimmedUppercaseSql) {
            if (!prefixApplied) {
                prefixApplied = true;
                // prefixesToOverride 非空，先删除
                if (prefixesToOverride != null) {
                    for (String toRemove : prefixesToOverride) {
                        if (trimmedUppercaseSql.startsWith(toRemove)) {
                            sql.delete(0, toRemove.trim().length());
                            break;
                        }
                    }
                }
                // prefix 非空，再添加
                if (prefix != null) {
                    sql.insert(0, " ");
                    sql.insert(0, prefix);
                }
            }
        }

        private void applySuffix(StringBuilder sql, String trimmedUppercaseSql) {
            if (!suffixApplied) {
                suffixApplied = true;
                // suffixesToOverride 非空，先删除
                if (suffixesToOverride != null) {
                    for (String toRemove : suffixesToOverride) {
                        if (trimmedUppercaseSql.endsWith(toRemove) || trimmedUppercaseSql.endsWith(toRemove.trim())) {
                            int start = sql.length() - toRemove.trim().length();
                            int end = sql.length();
                            sql.delete(start, end);
                            break;
                        }
                    }
                }
                // suffix 非空，再添加
                if (suffix != null) {
                    sql.append(" ");
                    sql.append(suffix);
                }
            }
        }

    }

}
