package com.joker.mybatis.reflection.invoker;

/**
 * <p>
 * 调用者接口：策略模式
 * 无论任何类型的反射调用，都离不开对象和入参，只要我们把这两个字段和返回结果定义的通用，就可以包住不同策略的实现类了。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/26
 */
public interface Invoker {

    /**
     * 通过反射，执行调用
     *
     * @param target 目标
     * @param args 参数
     * @return 结果
     * @throws IllegalAccessException
     */
    Object invoke(Object target, Object[] args) throws Exception;

    /**
     * @return 类
     */
    Class<?> getType();

}
