package com.joker.mybatis.mapping;

/**
 * <p>
 * Represents the content of a mapped statement read from an XML file or an annotation.
 * It creates the SQL that will be passed to the database out of the input parameter received from the user.
 * <p>
 * SQL 来源接口。它代表从 Mapper XML 或方法注解上，读取的一条 SQL 内容。
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/23
 */
public interface SqlSource {

    /**
     * <p>
     * 根据传入的参数对象，返回 BoundSql 对象
     * </P>
     *
     * @param parameterObject 参数对象
     * @return com.joker.mybatis.mapping.BoundSql
     * @author jokerzzccc
     * @date 2022/11/23
     */
    BoundSql getBoundSql(Object parameterObject);

}
