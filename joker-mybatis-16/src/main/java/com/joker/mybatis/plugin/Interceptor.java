package com.joker.mybatis.plugin;

import java.util.Properties;

/**
 * <p>
 * 拦截器接口
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/8
 */
public interface Interceptor {

    /**
     * 拦截方法（使用方实现）
     *
     * @param invocation 调用信息
     * @return 调用结果
     * @throws Throwable 若发生异常
     */
    Object intercept(Invocation invocation) throws Throwable;

    /**
     * 代理：
     * 应用插件。如应用成功，则会创建目标对象的代理对象
     *
     * @param target 目标对象
     * @return 应用的结果对象，可以是代理对象，也可以是 target 对象，也可以是任意对象。具体的，看代码实现
     */
    default Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置拦截器属性
     *
     * @param properties 属性
     */
    default void setProperties(Properties properties) {
        // NOP
    }

}
