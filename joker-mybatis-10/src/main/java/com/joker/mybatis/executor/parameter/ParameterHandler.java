package com.joker.mybatis.executor.parameter;


import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <p>
 * 参数处理器顶层接口
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/29
 */
public interface ParameterHandler {

    /**
     * 获取参数
     *
     * @return 参数对象
     */
    Object getParameterObject();

    /**
     * 设置参数：
     * 设置 PreparedStatement 的占位符参数
     *
     * @param ps PreparedStatement 对象
     * @throws SQLException 发生 SQL 异常时
     */
    void setParameters(PreparedStatement ps) throws SQLException;

}
