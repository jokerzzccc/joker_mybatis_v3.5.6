package com.joker.mybatis.test;

import com.alibaba.fastjson.JSON;
import com.joker.mybatis.io.Resources;
import com.joker.mybatis.session.SqlSession;
import com.joker.mybatis.session.SqlSessionFactory;
import com.joker.mybatis.session.SqlSessionFactoryBuilder;
import com.joker.mybatis.test.dao.IUserDao;
import com.joker.mybatis.test.po.User;
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

    /**
     * 在无池化和有池化的测试中，基础的单元测试类不需要改变，仍是通过 SqlSessionFactory 中获取 SqlSession 并获得映射对象和执行方法调用。
     * 另外这里是添加了50次的查询调用，便于验证连接池的创建和获取以及等待。
     * 变化的在于 mybatis-config-datasource.xml 中 dataSource 数据源类型的调整 dataSource type="POOLED/UNPOOLED"
     * @throws IOException
     */
    @Test
    public void test_SqlSessionFactory() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 3. 测试验证
        User user = userDao.queryUserInfoById(1L);
        logger.info("测试结果：{}", JSON.toJSONString(user));
    }

}
