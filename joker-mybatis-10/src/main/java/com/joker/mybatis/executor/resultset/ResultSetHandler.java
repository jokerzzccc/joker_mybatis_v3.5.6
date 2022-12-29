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

    /**
     * 处理 {@link java.sql.ResultSet} 结果集，转换成映射的对应的结果
     *
     * @param stmt Statement 对象
     * @param <E> 泛型
     * @return 结果数组
     */
    <E> List<E> handleResultSets(Statement stmt) throws SQLException;

}
