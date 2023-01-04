package com.joker.mybatis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * insert 语句注解
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Insert {

    /**
     * Returns an SQL for inserting record(s).
     *
     * @return an SQL for inserting record(s)
     */
    String[] value();

}
