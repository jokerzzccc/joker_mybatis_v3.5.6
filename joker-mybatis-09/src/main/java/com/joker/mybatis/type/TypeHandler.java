package com.joker.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <p>
 * 类型转换处理器:
 * 策略模式，
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/20
 */
public interface TypeHandler<T> {

    /**
     * 设置 PreparedStatement 的指定参数
     * <p>
     * Java Type => JDBC Type
     *
     * @param ps PreparedStatement 对象
     * @param i 参数占位符的位置
     * @param parameter 参数
     * @param jdbcType JDBC 类型
     * @throws SQLException 当发生 SQL 异常时
     * @author jokerzzccc
     * @date 2022/11/20
     */
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

}
