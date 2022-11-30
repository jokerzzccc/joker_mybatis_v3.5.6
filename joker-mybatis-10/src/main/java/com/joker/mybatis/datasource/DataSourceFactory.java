package com.joker.mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * <p>
 * 数据源工厂
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/18
 */
public interface DataSourceFactory {

    void setProperties(Properties props);

    DataSource getDataSource();

}
