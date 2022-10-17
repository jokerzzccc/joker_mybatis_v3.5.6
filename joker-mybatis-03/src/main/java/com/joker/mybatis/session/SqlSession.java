package com.joker.mybatis.session;

/**
 * <p>
 * The primary Java interface for working with MyBatis.
 * Through this interface you can execute commands, get mappers and manage transactions.
 * </p>
 * 用于定义 执行 SQL 标准、获取映射器以及管理事务等方面的操作。
 *
 * @author jokerzzccc
 * @date 2022/10/17
 */
public interface SqlSession {

    /**
     * Retrieve a single row mapped from the statement key
     * 根据指定的 SqlID 获取一条记录的封装对象
     *
     * @param statement sqlID
     * @param <T> the returned object type 封装之后的对象类型
     * @return Mapped object 封装之后的对象
     */
    <T> T selectOne(String statement);

    /**
     * Retrieve a single row mapped from the statement key and parameter.
     * 根据指定的 SqlID 获取一条记录的封装对象，只不过这个方法容许我们可以给 sql 传递一些参数；
     * 一般在实际使用中，这个参数传递的是： POJO, MAP, ImmutableMap...
     *
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param <T> the returned object type
     * @return Mapped object
     */
    <T> T selectOne(String statement, Object parameter);

    /**
     * Retrieves a mapper.
     * 得到映射器;
     * 这个巧妙的使用了泛型，使得类型安全
     *
     * @param <T> the mapper type
     * @param type Mapper interface class
     * @return a mapper bound to this SqlSession
     */
    <T> T getMapper(Class<T> type);

}
