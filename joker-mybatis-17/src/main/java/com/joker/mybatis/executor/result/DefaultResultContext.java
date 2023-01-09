package com.joker.mybatis.executor.result;

import com.joker.mybatis.session.ResultContext;

/**
 * <p>
 * 默认结果上下文
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/12/28
 */
public class DefaultResultContext implements ResultContext {

    private Object resultObject;
    private int resultCount;

    public DefaultResultContext() {
        this.resultObject = null;
        this.resultCount = 0;
    }

    @Override
    public Object getResultObject() {
        return resultObject;
    }

    @Override
    public int getResultCount() {
        return resultCount;
    }

    public void nextResultObject(Object resultObject) {
        resultCount++;
        this.resultObject = resultObject;
    }

}
