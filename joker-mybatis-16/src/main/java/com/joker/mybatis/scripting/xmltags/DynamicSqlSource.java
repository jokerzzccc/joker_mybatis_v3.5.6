package com.joker.mybatis.scripting.xmltags;

import com.joker.mybatis.builder.SqlSourceBuilder;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.SqlSource;
import com.joker.mybatis.session.Configuration;

import java.util.Map;

/**
 * <p>
 * 动态 SQL 源码处理
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/8
 */
public class DynamicSqlSource implements SqlSource {

    private Configuration configuration;
    private SqlNode rootSqlNode;

    public DynamicSqlSource(Configuration configuration, SqlNode rootSqlNode) {
        this.configuration = configuration;
        this.rootSqlNode = rootSqlNode;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        // 生成一个 DynamicContext 动态上下文
        DynamicContext context = new DynamicContext(configuration, parameterObject);
        // SqlNode.apply 将 ${} 参数替换掉，不替换 #{} 这种参数
        rootSqlNode.apply(context);

        // 创建 SqlSourceBuilder
        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        // 解析出 SqlSource 对象
        Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
        // SqlSourceBuilder.parse 这里返回的是 StaticSqlSource，解析过程就把那些参数都替换成?了，也就是最基本的JDBC的SQL语句。
        SqlSource sqlSource = sqlSourceParser.parse(context.getSql(), parameterType, context.getBindings());
        //  获得 BoundSql 对象: SqlSource.getBoundSql，非递归调用，而是调用 StaticSqlSource 实现类
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        // 添加附加参数到 BoundSql 对象中
        for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
            boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
        }

        return boundSql;
    }

}
