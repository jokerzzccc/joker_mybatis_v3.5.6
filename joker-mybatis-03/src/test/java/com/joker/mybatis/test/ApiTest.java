package com.joker.mybatis.test;

import com.joker.mybatis.binding.MapperProxyFactory;
import com.joker.mybatis.binding.MapperRegistry;
import com.joker.mybatis.session.SqlSession;
import com.joker.mybatis.session.defaults.DefaultSqlSessionFactory;
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
    public void test_SqlSessionFactory() {
        // 1. 注册 Mapper
        final MapperRegistry registry = new MapperRegistry();
        registry.addMappers("com.joker.mybatis.test.dao");

        // 2. 从 SqlSessionFactory 获取 SqlSession
        final DefaultSqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(registry);
        final SqlSession sqlSession = sqlSessionFactory.openSession();

        // 3. 获取映射器对象(Mapper)
        final IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 4. 测试验证
        final String res = userDao.queryUserName("10001");
        logger.info("测试结果：{}", res);
    }

}
