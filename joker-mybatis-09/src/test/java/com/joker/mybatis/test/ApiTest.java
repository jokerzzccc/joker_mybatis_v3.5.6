package com.joker.mybatis.test;

import com.alibaba.fastjson.JSON;
import com.joker.mybatis.io.Resources;
import com.joker.mybatis.session.SqlSession;
import com.joker.mybatis.session.SqlSessionFactory;
import com.joker.mybatis.session.SqlSessionFactoryBuilder;
import com.joker.mybatis.test.dao.IUserDao;
import com.joker.mybatis.test.po.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    private SqlSession sqlSession;

    @Before
    public void init() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        sqlSession = sqlSessionFactory.openSession();
    }

    /**
     * 基本类型参数测试
     *
     * @throws Exception
     */
    @Test
    public void test_queryUserInfoById() throws Exception {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        // 2. 测试验证：基本参数
        User user = userDao.queryUserInfoById(1l);
        logger.info("测试结果：{}", JSON.toJSONString(user));
    }

    /**
     * 对象类型参数测试
     *
     * @throws Exception
     */
    @Test
    public void test_queryUserInfo() {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        // 2. 测试验证：基本参数
        User user = userDao.queryUserInfo(new User(1L, "10001"));
        logger.info("测试结果：{}", JSON.toJSONString(user));
    }

}
