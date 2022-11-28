package com.joker.mybatis.reflection;

import com.joker.mybatis.reflection.factory.DefaultObjectFactory;
import com.joker.mybatis.reflection.factory.ObjectFactory;
import com.joker.mybatis.reflection.wrapper.DefaultObjectWrapperFactory;
import com.joker.mybatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * <p>
 * 一些系统级别的元对象:
 * 系统级的 MetaObject 对象:
 * 要提供了 ObjectFactory、ObjectWrapperFactory、空 MetaObject 的单例。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/29
 */
public class SystemMetaObject {
    /**
     * ObjectFactory 的单例
     */
    public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    /**
     * ObjectWrapperFactory 的单例
     */
    public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    /**
     * 空对象的 MetaObject 对象单例
     */
    public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(NullObject.class, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);

    private SystemMetaObject() {
        // Prevent Instantiation of Static Class
    }

    /**
     * 空对象
     */
    private static class NullObject {
    }

    /**
     * 创建 MetaObject 对象
     *
     * @param object 指定对象
     * @return MetaObject 对象
     */
    public static MetaObject forObject(Object object) {
        return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
    }


}
