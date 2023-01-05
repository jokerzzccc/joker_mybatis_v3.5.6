package com.joker.mybatis.session;

/**
 * <p>
 * 结果上下文
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/12/28
 */
public interface ResultContext {

    /**
     * 获取结果
     */
    Object getResultObject();

    /**
     * 获取记录数
     */
    int getResultCount();

}
