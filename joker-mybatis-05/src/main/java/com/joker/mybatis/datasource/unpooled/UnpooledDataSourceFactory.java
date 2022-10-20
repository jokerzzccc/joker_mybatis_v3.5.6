package com.joker.mybatis.datasource.unpooled;

import com.joker.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;


/**
 * <p>
 * 无连接池的数据源工厂
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/20
 */
public class UnpooledDataSourceFactory implements DataSourceFactory {

    protected Properties props;

    @Override
    public void setProperties(Properties props) {
        this.props = props;
    }

    @Override
    public DataSource getDataSource() {
        UnpooledDataSource unpooledDataSource = new UnpooledDataSource();
        unpooledDataSource.setDriver(props.getProperty("driver"));
        unpooledDataSource.setUrl(props.getProperty("url"));
        unpooledDataSource.setUsername(props.getProperty("username"));
        unpooledDataSource.setPassword(props.getProperty("password"));
        return unpooledDataSource;
    }

}
