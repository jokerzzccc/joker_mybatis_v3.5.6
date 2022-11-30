package com.joker.mybatis.executor.statement;

import com.joker.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * <p>
 * 语句处理器的核心包括了；准备语句、参数化传递参数、执行查询的操作，
 * 这里对应的 Mybatis 源码中还包括了 update、批处理、获取参数处理器等。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/22
 */
public interface StatementHandler {

    /**
     * 准备语句
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    Statement prepare(Connection connection) throws SQLException;

    /**
     * 参数化
     *
     * @param statement
     * @throws SQLException
     */
    void parameterize(Statement statement) throws SQLException;

    /**
     * 执行查询
     *
     * @param statement
     * @param resultHandler
     * @param <E>
     * @return
     * @throws SQLException
     */
    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

}
