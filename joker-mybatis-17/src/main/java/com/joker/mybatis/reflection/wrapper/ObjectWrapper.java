package com.joker.mybatis.reflection.wrapper;

import com.joker.mybatis.reflection.MetaClass;
import com.joker.mybatis.reflection.MetaObject;
import com.joker.mybatis.reflection.factory.ObjectFactory;
import com.joker.mybatis.reflection.property.PropertyTokenizer;

import java.util.List;

/**
 * <p>
 * 对象包装器接口: 基于 MetaClass 工具类，定义对指定对象的各种操作。定义了更加明确的需要使用的方法，
 * 包括定义出了 get/set 标准的通用方法、获取get\set属性名称和属性类型，以及添加属性等操作。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/29
 */
public interface ObjectWrapper {

    /**
     * 获得指定属性的值
     *
     * @param prop PropertyTokenizer 对象，相当于键
     * @return 值
     */
    Object get(PropertyTokenizer prop);

    /**
     * 设置指定属性的值
     *
     * @param prop PropertyTokenizer 对象，相当于键
     * @param value 值
     */
    void set(PropertyTokenizer prop, Object value);

    /**
     * 查找属性
     * {@link MetaClass#findProperty(String, boolean)}
     */
    String findProperty(String name, boolean useCamelCaseMapping);

    /**
     * 取得 getter 的名字列表
     * {@link MetaClass#getGetterNames()}
     */
    String[] getGetterNames();

    /**
     * 取得 setter 的类型
     * {@link MetaClass#getSetterNames()}
     */
    String[] getSetterNames();

    /**
     * 取得 setter 的类型
     * {@link MetaClass#getSetterType(String)}
     */
    Class<?> getSetterType(String name);

    /**
     * 取得 getter 的类型
     * {@link MetaClass#getGetterType(String)}
     */
    Class<?> getGetterType(String name);

    /**
     * 是否有指定的getter
     * {@link MetaClass#hasGetter(String)}
     */
    boolean hasGetter(String name);

    /**
     * 是否有指定的 setter
     * {@link MetaClass#hasSetter(String)}
     */
    boolean hasSetter(String name);

    /**
     * 实例化属性
     * {@link MetaObject#forObject(Object, ObjectFactory, ObjectWrapperFactory)}
     */
    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

    /**
     * 是否为集合
     */
    boolean isCollection();

    /**
     * 添加元素到集合
     */
    void add(Object element);

    /**
     * 添加多个元素到集合
     */
    <E> void addAll(List<E> element);

}
