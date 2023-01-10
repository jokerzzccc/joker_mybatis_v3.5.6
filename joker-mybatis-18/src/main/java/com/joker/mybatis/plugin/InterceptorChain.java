package com.joker.mybatis.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 拦截器链（Interceptor 链）
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/8
 */
public class InterceptorChain {

    /**
     * 拦截器集合
     */
    private final List<Interceptor> interceptors = new ArrayList<>();

    /**
     * 应用所有拦截器到指定目标对象,
     * 一共可以有四种目标对象类型可以被拦截：
     * 1）Executor；2）StatementHandler；3）ParameterHandler；4）ResultSetHandler 。
     *
     * @param target
     * @return
     */
    public Object pluginAll(Object target) {
        for (Interceptor interceptor : interceptors) {
            target = interceptor.plugin(target);
        }
        return target;
    }

    /**
     * 添加拦截器
     * 该方法在 Configuration#pluginElement 方法中被调用
     * @param interceptor
     */
    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }

}
