package com.joker.mybatis.session;

/**
 * <p>
 * 本地缓存机制：
 * SESSION 默认值，缓存一个会话中执行的所有查询
 * STATEMENT 本地会话仅用在语句执行上，对相同 SqlSession 的不同调用将不做数据共享
 *
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/9
 */
public enum LocalCacheScope {
    /**
     * SESSION为默认值，支持使用一级缓存
     */
    SESSION,
    /**
     * STATEMENT不支持使用一级缓存，这部分具体的判断使用可以参考源码
     */
    STATEMENT
}
