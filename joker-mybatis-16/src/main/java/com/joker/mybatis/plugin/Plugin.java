package com.joker.mybatis.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 代理模式插件：实现 InvocationHandler 接口，插件类，
 * 一方面提供创建动态代理对象的方法，另一方面实现对指定类的指定方法的拦截处理。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/8
 */
public class Plugin implements InvocationHandler {

    /**
     * 目标对象
     */
    private final Object target;
    /**
     * 拦截器
     */
    private final Interceptor interceptor;
    /**
     * 拦截的方法映射
     * <p>
     * KEY：类
     * VALUE：方法集合
     */
    private final Map<Class<?>, Set<Method>> signatureMap;

    private Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
        this.target = target;
        this.interceptor = interceptor;
        this.signatureMap = signatureMap;
    }

    /**
     * 最终对于插件的核心调用，都会体现到 invoke 方法中。
     * 如一个被代理的类 ParameterHandler 当调用它的方法时，都会进入 invoke 中。
     * 在 invoke 方法中，通过前面方法的判断确定使用方自己实现的插件，是否在比时调用的方法上。
     * 如果是则进入插件调用，插件的实现中处理完自己的逻辑则进行invocation..proceed();放行。
     * 如果不在这个方法上，则直接通过method..invoke(target,.args);调用原本的方法即可。
     * 这样就达到了扩展插件的目的。
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取声明的方法列表
        Set<Method> methods = signatureMap.get(method.getDeclaringClass());
        // 过滤需要拦截的方法
        if (null != methods && methods.contains(method)) {
            // 调用 Interceptor#intercept 插入自己的反射逻辑
            return interceptor.intercept(new Invocation(target, method, args));
        }
        return method.invoke(target, args);
    }

    /**
     * 用代理把自定义插件行为包裹到目标方法中，也就是 Plugin.invoke 的过滤调用:
     * wrap方法是用于给ParameterHandler、.ResultSetHandler、StatementHandler、Executor创建代理类时调用的。
     * 而这个创建的目的，就是把插件内容，包装到代理中。
     */
    public static Object wrap(Object target, Interceptor interceptor) {
        // 获得拦截的方法签名映射
        Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
        // 获得目标类的类型
        // 取得要改变行为的类(ParameterHandler|ResultSetHandler|StatementHandler|Executor)，目前只添加了 StatementHandler
        Class<?> type = target.getClass();
        // 获得目标类的接口集合
        Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
        // 若有接口，则创建目标对象的 JDK Proxy 对象
        // 创建代理(StatementHandler)
        if (interfaces.length > 0) {
            // Proxy.newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)
            return Proxy.newProxyInstance(
                    type.getClassLoader(),
                    interfaces,
                    new Plugin(target, interceptor, signatureMap));
        }
        // 若无接口，则返回原始的目标对象。
        return target;
    }

    /**
     * 获得拦截的方法签名映射:
     * getSignatureMap所完成的动作就是为了获取代理类的签名操作，返回这个类下在哪个方法下执行调用插件操作
     */
    private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) {
        // 取 Intercepts 注解，例子可参见 TestPlugin.java
        Intercepts interceptsAnnotation = interceptor.getClass().getAnnotation(Intercepts.class);
        // 必须得有 Intercepts 注解，没有报错
        if (interceptsAnnotation == null) {
            throw new RuntimeException("No @Intercepts annotation was found in interceptor " + interceptor.getClass().getName());
        }
        // value是数组型，Signature的数组
        Signature[] sigs = interceptsAnnotation.value();
        // 每个 class 类有多个可能有多个 Method 需要被拦截
        Map<Class<?>, Set<Method>> signatureMap = new HashMap<>();
        for (Signature sig : sigs) {
            Set<Method> methods = signatureMap.computeIfAbsent(sig.type(), k -> new HashSet<>());
            try {
                // 例如获取到方法；StatementHandler.prepare(Connection connection)、StatementHandler.parameterize(Statement statement)...
                Method method = sig.type().getMethod(sig.method(), sig.args());
                methods.add(method);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Could not find method on " + sig.type() + " named " + sig.method() + ". Cause: " + e, e);
            }
        }
        return signatureMap;
    }

    /**
     * 获得目标类的接口集合
     */
    private static Class<?>[] getAllInterfaces(Class<?> type, Map<Class<?>, Set<Method>> signatureMap) {
        // 接口的集合
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        // 循环递归 type 类，机器父类
        while (type != null) {
            // 遍历接口集合，若在 signatureMap 中，则添加到 interfaces 中
            for (Class<?> c : type.getInterfaces()) {
                // 拦截 ParameterHandler|ResultSetHandler|StatementHandler|Executor
                if (signatureMap.containsKey(c)) {
                    interfaces.add(c);
                }
            }
            // 获得父类
            type = type.getSuperclass();
        }
        // 创建接口的数组
        return interfaces.toArray(new Class<?>[interfaces.size()]);
    }

}
