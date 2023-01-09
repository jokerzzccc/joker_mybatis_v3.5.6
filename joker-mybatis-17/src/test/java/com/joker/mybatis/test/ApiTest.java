package com.joker.mybatis.test;

import com.alibaba.fastjson.JSON;
import com.joker.mybatis.builder.xml.XMLConfigBuilder;
import com.joker.mybatis.executor.Executor;
import com.joker.mybatis.io.Resources;
import com.joker.mybatis.mapping.Environment;
import com.joker.mybatis.session.*;
import com.joker.mybatis.session.defaults.DefaultSqlSession;
import com.joker.mybatis.test.dao.IActivityDao;
import com.joker.mybatis.test.po.Activity;
import com.joker.mybatis.transaction.Transaction;
import com.joker.mybatis.transaction.TransactionFactory;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

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
    public void test_queryActivityById() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 2. 获取映射器对象
        IActivityDao dao = sqlSession.getMapper(IActivityDao.class);
        // 3. 测试验证
        Activity req = new Activity();
        req.setActivityId(100001L);
        logger.info("测试结果：{}", JSON.toJSONString(dao.queryActivityById(req)));

//         sqlSession.commit();
        // sqlSession.clearCache();
         sqlSession.close();

        logger.info("测试结果：{}", JSON.toJSONString(dao.queryActivityById(req)));

    }

}
