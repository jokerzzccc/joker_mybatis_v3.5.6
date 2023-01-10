package com.joker.mybatis.plugin;

/**
 * <p>
 * 方法签名的注解:
 * 用于定位需要在哪个类的哪个方法下完成插件的调用。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/8
 */
public @interface Signature {

    /**
     * 被拦截类
     *
     * @return 类
     */
    Class<?> type();

    /**
     * 被拦截类的方法
     *
     * @return 方法名
     */
    String method();

    /**
     * 被拦截类的方法的参数
     *
     * @return 参数类型
     */
    Class<?>[] args();

}
