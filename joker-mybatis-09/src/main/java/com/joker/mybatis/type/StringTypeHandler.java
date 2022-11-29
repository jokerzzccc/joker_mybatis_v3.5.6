package com.joker.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <p>
 * String类型处理器
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/28
 */
public class StringTypeHandler extends BaseTypeHandler<String> {

    @Override
    protected void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter);
    }

}
