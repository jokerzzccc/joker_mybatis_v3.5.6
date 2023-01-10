package com.joker.mybatis.mapping;

import com.joker.mybatis.session.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * <p>
 * 结果集映射:
 * ResultMap 就是一个简单的返回结果信息映射类，并提供了建造者方法，方便外部使用.
 * ResultMap映射对象的封装主要包括了对象的构建和结果的存放，
 * 存放的地点就是Configuration配置项中所提供的结果映射Map Map<String,ResultMap>resultMaps;
 * 这样的配置方式也是为了后续可以通过resultMaps Key获取到对应的ResultMap进行使用
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/12/28
 */
public class ResultMap {

    private String id;
    private Class<?> type;
    private List<ResultMapping> resultMappings;
    private Set<String> mappedColumns;

    private ResultMap() {
    }

    /**
     * 负责完成字段的处理：
     * 除 mappedColumns, 其它的都可以通过构造函数传递
     */
    public static class Builder {

        private ResultMap resultMap = new ResultMap();

        public Builder(Configuration configuration, String id, Class<?> type, List<ResultMapping> resultMappings) {
            resultMap.id = id;
            resultMap.type = type;
            resultMap.resultMappings = resultMappings;
        }

        public ResultMap build() {
            resultMap.mappedColumns = new HashSet<>();
            // 添加 mappedColumns 字段
            for (ResultMapping resultMapping : resultMap.resultMappings) {
                final String column = resultMapping.getColumn();
                if (column != null) {
                    resultMap.mappedColumns.add(column.toUpperCase(Locale.ENGLISH));
                }
            }
            return resultMap;
        }

    }

    public String getId() {
        return id;
    }

    public Set<String> getMappedColumns() {
        return mappedColumns;
    }

    public Class<?> getType() {
        return type;
    }

    public List<ResultMapping> getResultMappings() {
        return resultMappings;
    }

    public List<ResultMapping> getPropertyResultMappings() {
        return resultMappings;
    }

}
