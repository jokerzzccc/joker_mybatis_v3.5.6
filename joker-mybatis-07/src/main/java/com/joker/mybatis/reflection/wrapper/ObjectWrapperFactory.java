package com.joker.mybatis.reflection.wrapper;

import com.joker.mybatis.reflection.MetaObject;

/**
 * <p>
 * ObjectWrapper 工厂接口
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/29
 */
public interface ObjectWrapperFactory {

    /**
     * 是否包装了指定对象
     *
     * @param object 指定对象
     * @return 是否
     */
    boolean hasWrapperFor(Object object);

    /**
     * 获得指定对象的 ObjectWrapper 对象
     *
     * @param metaObject MetaObject 对象
     * @param object 指定对象
     * @return ObjectWrapper 对象
     */
    ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);
}
