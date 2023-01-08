package com.joker.mybatis.test.plugin;

import com.joker.mybatis.executor.statement.StatementHandler;
import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.plugin.*;

import java.sql.Connection;
import java.util.Properties;

/**
 * <p>
 * TestPlugin自定义插件实现Interceptor接口，同时通过注解@Intercepts配置插件的触发时机。
 * 这里则是在调用StatementHandler#prepare方法时，处理自定义插件的操作。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/8
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class TestPlugin implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取StatementHandler
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        // 获取SQL信息
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        // 输出SQL
        System.out.println("拦截SQL：" + sql);
        // 放行
        return invocation.proceed();
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println("参数输出：" + properties.getProperty("test00"));
    }

}
