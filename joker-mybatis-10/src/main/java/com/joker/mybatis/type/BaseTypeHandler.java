package com.joker.mybatis.type;

import com.joker.mybatis.session.Configuration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 * TypeHandler 基础抽象类： 类型处理器的基类
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/28
 */
public abstract class BaseTypeHandler<T> implements TypeHandler<T> {

    protected Configuration configuration;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        setNonNullParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        return getNullableResult(rs, columnName);
    }

    /**
     * <p>
     * 定义抽象方法，由子类实现不同类型的设置
     * </P>
     */
    protected abstract void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

    protected abstract T getNullableResult(ResultSet rs, String columnName) throws SQLException;

}
