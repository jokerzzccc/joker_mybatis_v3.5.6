package com.joker.mybatis.executor;

import com.joker.mybatis.mapping.BoundSql;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.session.ResultHandler;
import com.joker.mybatis.session.RowBounds;
import com.joker.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 * 执行器入口: 确定出事务和操作和 SQL 执行的统一标准接口。
 * </p>
 *  在执行器中定义的接口包括事务相关的处理方法和执行SQL查询的操作，随着后续功能的迭代还会继续补充其他的方法。
 * @author jokerzzccc
 * @date 2022/10/22
 */
public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql);

    Transaction getTransaction();

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    void close(boolean forceRollback);


}
