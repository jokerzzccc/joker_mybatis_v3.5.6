package com.joker.mybatis.reflection.invoker;

import java.lang.reflect.Field;

/**
 * <p>
 * getter 方法的调用者处理，因为get是有返回值的，所以直接对 Field 字段操作完后直接返回结果。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/26
 */
public class GetFieldInvoker implements Invoker {

    private Field field;

    public GetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        return field.get(target);
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

}
