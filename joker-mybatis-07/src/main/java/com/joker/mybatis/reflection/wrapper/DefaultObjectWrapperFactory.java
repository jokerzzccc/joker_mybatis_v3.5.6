package com.joker.mybatis.reflection.wrapper;

import com.joker.mybatis.reflection.MetaObject;

/**
 * <p>
 * 默认 ObjectWrapperFactory 实现类对象
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/29
 */
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory{

    @Override
    public boolean hasWrapperFor(Object object) {
        return false;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        throw new RuntimeException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }

}
