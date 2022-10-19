package com.joker.mybatis.session;

/**
 * <p>
 * Creates an {@link SqlSession} out of a connection or a DataSource
 * </p>
 * SqlSessionFactory 工厂中提供的开启 SqlSession 的能力。
 *
 * @author jokerzzccc
 * @date 2022/10/17
 */
public interface SqlSessionFactory {

    /**
     * 打开一个 session
     *
     * @return SqlSession
     */
    SqlSession openSession();

}
