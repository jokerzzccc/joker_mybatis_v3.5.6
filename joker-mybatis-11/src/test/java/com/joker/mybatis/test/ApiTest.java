package com.joker.mybatis.test;

import java.util.Date;

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
import java.util.List;

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

    @Test
    public void test_insertUserInfo() {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 2. 测试验证
        User user = new User();
        user.setUserId("10002");
        user.setUserName("joker02");
        user.setUserHead("1_02");
        userDao.insertUserInfo(user);
        logger.info("测试结果：{}", "Insert OK");

        // 3. 提交事务
        // 在 DefaultSqlSessionFactory#openSession 开启 Session 创建事务工厂的时候，传入给事务工厂构造函数的事务是否自动提交为 false
        // 所以这里就需要我们自己去手动提交事务
        sqlSession.commit();
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

    @Test
    public void test_deleteUserInfoByUserId() {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        // 2. 测试验证
        int count = userDao.deleteUserInfoByUserId("10001");
        logger.info("测试结果：{}", count == 1);
        // 3. 提交事务
        sqlSession.commit();
    }

    @Test
    public void test_updateUserInfo() {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        // 2. 测试验证
        int count = userDao.updateUserInfo(new User(1L, "10001", "zoro"));
        logger.info("测试结果：{}", count);
        // 3. 提交事务
        sqlSession.commit();
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

    @Test
    public void test_queryUserInfoList() {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        // 2. 测试验证：对象参数
        List<User> users = userDao.queryUserInfoList();
        logger.info("测试结果：{}", JSON.toJSONString(users));
    }

}
