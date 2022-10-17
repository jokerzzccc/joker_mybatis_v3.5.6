package com.joker.mybatis.test;

import com.joker.mybatis.binding.MapperProxyFactory;
import com.joker.mybatis.test.dao.IUserDao;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void test_MapperProxyFactory() {
        MapperProxyFactory<IUserDao> factory = new MapperProxyFactory<>(IUserDao.class);
        Map<String, String> sqlSession = new HashMap<>();

        sqlSession.put("com.joker.mybatis.test.dao.IUserDao.queryUserName", "name: jokerzzccc(模拟执行 Mapper.xml 中 SQL 语句的操作：查询用户姓名)");
        sqlSession.put("com.joker.mybatis.test.dao.IUserDao.queryUserAge", "age: 1024(模拟执行 Mapper.xml 中 SQL 语句的操作：查询用户年龄)");
        IUserDao userDao = factory.newInstance(sqlSession);

        String resName = userDao.queryUserName("10001");
        String resAge = userDao.queryUserAge("10001");
        logger.info("测试结果：{}", resName);
        logger.info("测试结果：{}", resAge);

    }

    @Test
    public void test_proxy_class() {
        IUserDao userDao = (IUserDao) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{IUserDao.class}, (proxy, method, args) -> "你被代理了");
//        String result = userDao.queryUserName("10001");
        String result = userDao.queryUserAge("10001");
        System.out.println("测试结果：" + result);
    }

}
