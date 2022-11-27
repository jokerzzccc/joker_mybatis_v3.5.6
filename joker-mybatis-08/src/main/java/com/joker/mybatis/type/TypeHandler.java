package com.joker.mybatis.type;

import cn.hutool.db.meta.JdbcType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <p>
 * 类型处理器
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/20
 */
public interface TypeHandler<T> {

    /**
     * <p>
     * 设置参数
     * </P>
     *
     * @author jokerzzccc
     * @date 2022/11/20
     */
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

}
