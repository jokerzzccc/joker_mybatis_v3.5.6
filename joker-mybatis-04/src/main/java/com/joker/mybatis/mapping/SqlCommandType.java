package com.joker.mybatis.mapping;

/**
 * <p>
 * SQL 指令类型
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/18
 */
public enum SqlCommandType {
    /**
     * 未知
     */
    UNKNOWN,
    /**
     * 插入
     */
    INSERT,
    /**
     * 更新
     */
    UPDATE,
    /**
     * 删除
     */
    DELETE,
    /**
     * 查找
     */
    SELECT;

}
