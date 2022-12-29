package com.joker.mybatis.session;

/**
 * <p>
 * 结果处理器
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/22
 */
public interface ResultHandler {

    /**
     * 处理结果
     *
     * @param context 结果上下文
     */
    void handleResult(ResultContext context);

}
