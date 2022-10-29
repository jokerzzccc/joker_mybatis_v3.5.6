package com.joker.mybatis.datasource.pooled;

import com.joker.mybatis.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * <p>
 * 有连接池的数据源工厂
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/20
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

    public PooledDataSourceFactory() {
        this.dataSource = new PooledDataSource();
    }

}
