package com.joker.mybatis.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.joker.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * <p>
 * Druid 数据源工厂
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/18
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    /**
     * 属性文件：存放数据库连接信息
     */
    private Properties props;

    @Override
    public void setProperties(Properties props) {
        this.props = props;
    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(props.getProperty("driver"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setUsername(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));
        return dataSource;
    }

}
