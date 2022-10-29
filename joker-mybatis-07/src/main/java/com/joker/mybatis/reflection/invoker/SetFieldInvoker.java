package com.joker.mybatis.reflection.invoker;

import java.lang.reflect.Field;

/**
 * <p>
 * setter 方法的调用者处理，因为set只是设置值，所以这里就只返回一个 null 就可以了。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/26
 */
public class SetFieldInvoker implements Invoker {

    private Field field;

    public SetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        field.set(target, args[0]);
        return null;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

}
