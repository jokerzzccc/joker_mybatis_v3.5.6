package com.joker.mybatis.executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * <p>
 * 结果集处理器
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/22
 */
public interface ResultSetHandler {

    <E> List<E> handleResultSets(Statement stmt) throws SQLException;

}
