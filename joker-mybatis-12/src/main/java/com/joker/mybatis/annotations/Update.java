package com.joker.mybatis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * update 语句注解
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update {

    /**
     * Returns an SQL for updating record(s).
     *
     * @return an SQL for updating record(s)
     */
    String[] value();

}
