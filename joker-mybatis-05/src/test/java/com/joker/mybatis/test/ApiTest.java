package com.joker.mybatis.test;

import com.alibaba.fastjson.JSON;
import com.joker.mybatis.binding.MapperProxyFactory;
import com.joker.mybatis.binding.MapperRegistry;
import com.joker.mybatis.builder.xml.XMLConfigBuilder;
import com.joker.mybatis.io.Resources;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.session.SqlSession;
import com.joker.mybatis.session.SqlSessionFactory;
import com.joker.mybatis.session.SqlSessionFactoryBuilder;
import com.joker.mybatis.session.defaults.DefaultSqlSession;
import com.joker.mybatis.session.defaults.DefaultSqlSessionFactory;
import com.joker.mybatis.test.dao.IUserDao;
import com.joker.mybatis.test.po.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/16
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void test_SqlSessionFactory() throws IOException {
        // 1. 从 SqlSessionFactory 中获取 SqlSession
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 3. 测试验证
        User user = userDao.queryUserInfoById(1L);
        logger.info("测试结果：{}", JSON.toJSONString(user));
    }

    @Test
    public void test_selectOne() throws IOException {
        // 解析 XML
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        Configuration configuration = xmlConfigBuilder.parse();

        // 获取 DefaultSqlSession
        DefaultSqlSession sqlSession = new DefaultSqlSession(configuration);

        // 执行查询，默认是一个集合参数
        Object[] req = {1L};
        Object res = sqlSession.selectOne("com.joker.mybatis.test.dao.IUserDao.queryUserInfoById", req);
        logger.info("测试结果：{}", JSON.toJSONString(res));
    }

}
