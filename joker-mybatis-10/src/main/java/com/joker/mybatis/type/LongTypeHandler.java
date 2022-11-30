package com.joker.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <p>
 * Long 类型处理器
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/28
 */
public class LongTypeHandler extends BaseTypeHandler<Long> {

    @Override
    protected void setNonNullParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter);
    }

}
