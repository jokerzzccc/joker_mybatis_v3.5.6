package com.joker.mybatis.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 拦截器注解:
 * Intercepts注解一个目的是作为标记存在，所有的插件实现都需要有这个自定义的注解标记。
 * 另外这个注解中还有另外一个注解的存在，就是方法签名注解，用于定位需要在哪个类的哪个方法下完成插件的调用。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Intercepts {

    /**
     * @return 拦截的方法签名的数组
     */
    Signature[] value();

}
