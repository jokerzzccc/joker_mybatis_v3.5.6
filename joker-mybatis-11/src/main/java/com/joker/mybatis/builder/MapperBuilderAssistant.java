package com.joker.mybatis.builder;

import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.mapping.ResultMap;
import com.joker.mybatis.mapping.SqlCommandType;
import com.joker.mybatis.mapping.SqlSource;
import com.joker.mybatis.scripting.LanguageDriver;
import com.joker.mybatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 映射构建器助手，建造者
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/12/28
 */
public class MapperBuilderAssistant extends BaseBuilder {

    private String currentNamespace;
    private String resource;

    public MapperBuilderAssistant(Configuration configuration, String resource) {
        super(configuration);
        this.resource = resource;
    }

    public String getCurrentNamespace() {
        return currentNamespace;
    }

    public void setCurrentNamespace(String currentNamespace) {
        this.currentNamespace = currentNamespace;
    }

    public String applyCurrentNamespace(String base, boolean isReference) {
        if (base == null) {
            return null;
        }
        if (isReference) {
            if (base.contains(".")) return base;
        }
        return currentNamespace + "." + base;
    }

    /**
     * 添加映射器语句, 更加标准的封装了入参和出参信息。
     */
    public MappedStatement addMappedStatement(
            String id,
            SqlSource sqlSource,
            SqlCommandType sqlCommandType,
            Class<?> parameterType,
            String resultMap,
            Class<?> resultType,
            LanguageDriver lang
    ) {
        // 给id加上 namespace 前缀：com.joker.mybatis.test.dao.IUserDao.queryUserInfoById
        id = applyCurrentNamespace(id, false);
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlCommandType, sqlSource, resultType);

        // 结果映射，给 MappedStatement#resultMaps
        setStatementResultMap(resultMap, resultType, statementBuilder);

        MappedStatement statement = statementBuilder.build();
        // 映射语句信息，建造完存放到配置项中
        configuration.addMappedStatement(statement);

        return statement;

    }

    /**
     * 只是一个非常简单的结果映射建造的过程，无论是否为 ResultMap 都会进行这样的封装处理。
     * 并最终把创建的信息写入到 MappedStatement 映射语句类中。
     */
    private void setStatementResultMap(String resultMap, Class<?> resultType, MappedStatement.Builder statementBuilder) {
        // 因为暂时还没有在 Mapper XML 中配置 Map 返回结果，所以这里返回的是 null
        resultMap = applyCurrentNamespace(resultMap, true);

        List<ResultMap> resultMaps = new ArrayList<>();

        if (resultMap != null) {
            // TODO：暂无Map结果映射配置，本章节不添加此逻辑
        } else if (resultType != null) {
            ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(
                    configuration, statementBuilder.id() + "-Inline", resultType, new ArrayList<>());

            resultMaps.add(inlineResultMapBuilder.build());
        }
        statementBuilder.resultMaps(resultMaps);
    }

}
