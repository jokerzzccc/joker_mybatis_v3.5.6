package com.joker.mybatis.executor.keygen;

import com.joker.mybatis.executor.Executor;
import com.joker.mybatis.mapping.MappedStatement;

import java.sql.Statement;

/**
 * <p>
 * NoKeyGenerator:默认空实现，不对主键单独处理。即无需主键生成。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/7
 */
public class NoKeyGenerator implements KeyGenerator {

    /**
     * A shared instance.
     *
     * @since 3.4.3
     */
    public static final NoKeyGenerator INSTANCE = new NoKeyGenerator();

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // Do Nothing
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // Do Nothing
    }

}
