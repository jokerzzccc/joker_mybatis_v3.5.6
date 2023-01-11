package com.joker.mybatis.builder;

import com.joker.mybatis.cache.Cache;
import com.joker.mybatis.cache.decorators.FifoCache;
import com.joker.mybatis.cache.impl.PerpetualCache;
import com.joker.mybatis.executor.keygen.KeyGenerator;
import com.joker.mybatis.mapping.*;
import com.joker.mybatis.reflection.MetaClass;
import com.joker.mybatis.scripting.LanguageDriver;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.type.TypeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
    private Cache currentCache;

    public MapperBuilderAssistant(Configuration configuration, String resource) {
        super(configuration);
        this.resource = resource;
    }

    /**
     * 构造 ResultMapping 对象
     *
     * @param resultType
     * @param property
     * @param column
     * @param flags
     * @return
     */
    public ResultMapping buildResultMapping(Class<?> resultType, String property, String column, List<ResultFlag> flags) {
        // 解析对应的 Java Type 类和 TypeHandler 对象
        Class<?> javaTypeClass = resolveResultJavaType(resultType, property, null);
        TypeHandler<?> typeHandlerInstance = resolveTypeHandler(javaTypeClass, null);

        ResultMapping.Builder builder = new ResultMapping.Builder(configuration, property, column, javaTypeClass);
        builder.typeHandler(typeHandlerInstance);
        builder.flags(flags);

        return builder.build();
    }

    private Class<?> resolveResultJavaType(Class<?> resultType, String property, Class<?> javaType) {
        if (javaType == null && property != null) {
            try {
                MetaClass metaResultType = MetaClass.forClass(resultType);
                javaType = metaResultType.getSetterType(property);
            } catch (Exception ignore) {
            }
        }
        if (javaType == null) {
            javaType = Object.class;
        }
        return javaType;
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
        } else {
            if (base.startsWith(currentNamespace + ".")) {
                return base;
            }
            if (base.contains(".")) {
                throw new RuntimeException("Dots are not allowed in element names, please remove it from " + base);
            }
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
            boolean flushCache,
            boolean useCache,
            KeyGenerator keyGenerator,
            String keyProperty,
            LanguageDriver lang
    ) {
        // 给id加上 namespace 前缀：com.joker.mybatis.test.dao.IUserDao.queryUserInfoById
        id = applyCurrentNamespace(id, false);
        //是否是select语句
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlCommandType, sqlSource, resultType);
        statementBuilder.resource(resource);
        statementBuilder.keyGenerator(keyGenerator);
        statementBuilder.keyProperty(keyProperty);

        // 结果映射，给 MappedStatement#resultMaps
        setStatementResultMap(resultMap, resultType, statementBuilder);
        setStatementCache(isSelect, flushCache, useCache, currentCache, statementBuilder);

        MappedStatement statement = statementBuilder.build();
        // 映射语句信息，建造完存放到配置项中
        configuration.addMappedStatement(statement);

        return statement;

    }
    private void setStatementCache(
            boolean isSelect,
            boolean flushCache,
            boolean useCache,
            Cache cache,
            MappedStatement.Builder statementBuilder) {
        flushCache = valueOrDefault(flushCache, !isSelect);
        useCache = valueOrDefault(useCache, isSelect);
        statementBuilder.flushCacheRequired(flushCache);
        statementBuilder.useCache(useCache);
        statementBuilder.cache(cache);
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
            String[] resultMapNames = resultMap.split(",");
            for (String resultMapName : resultMapNames) {
                resultMaps.add(configuration.getResultMap(resultMapName.trim()));
            }
        } else if (resultType != null) {
            ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(
                    configuration, statementBuilder.id() + "-Inline", resultType, new ArrayList<>());

            resultMaps.add(inlineResultMapBuilder.build());
        }
        statementBuilder.resultMaps(resultMaps);
    }

    public ResultMap addResultMap(String id, Class<?> type, List<ResultMapping> resultMappings) {
        // 补全ID全路径，如：com.joker.mybatis.test.dao.IActivityDao + activityMap
        id = applyCurrentNamespace(id, false);

        ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(
                configuration,
                id,
                type,
                resultMappings);

        ResultMap resultMap = inlineResultMapBuilder.build();
        configuration.addResultMap(resultMap);
        return resultMap;
    }

    public Cache useNewCache(Class<? extends Cache> typeClass,
            Class<? extends Cache> evictionClass,
            Long flushInterval,
            Integer size,
            boolean readWrite,
            boolean blocking,
            Properties props) {
        // 判断为null，则用默认值
        typeClass = valueOrDefault(typeClass, PerpetualCache.class);
        evictionClass = valueOrDefault(evictionClass, FifoCache.class);

        // 建造者模式构建 Cache [currentNamespace=cn.bugstack.mybatis.test.dao.IActivityDao]
        Cache cache = new CacheBuilder(currentNamespace)
                .implementation(typeClass)
                .addDecorator(evictionClass)
                .clearInterval(flushInterval)
                .size(size)
                .readWrite(readWrite)
                .blocking(blocking)
                .properties(props)
                .build();

        // 添加缓存
        configuration.addCache(cache);
        currentCache = cache;
        return cache;
    }

    private <T> T valueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

}
