package com.joker.mybatis.parsing;

/**
 * <p>
 * 记号处理器: Token 处理器接口
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/24
 */
public interface TokenHandler {

    /**
     * 处理 Token
     *
     * @param content Token 字符串
     * @return 处理后的结果
     */
    String handleToken(String content);

}

