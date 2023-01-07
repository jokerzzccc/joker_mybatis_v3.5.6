package com.joker.mybatis.builder;

import com.joker.mybatis.mapping.ResultMap;
import com.joker.mybatis.mapping.ResultMapping;

import java.util.List;

/**
 * <p>
 * ResultMap 解析器，即结果映射解析器： 对解析结果内容的一个封装处理，
 * 最终还是调用 MapperBuilderAssistant 所提供的 ResultMap 封装和保存操作。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/7
 */
public class ResultMapResolver {

    private final MapperBuilderAssistant assistant;
    private String id;
    private Class<?> type;
    private List<ResultMapping> resultMappings;

    public ResultMapResolver(MapperBuilderAssistant assistant, String id, Class<?> type, List<ResultMapping> resultMappings) {
        this.assistant = assistant;
        this.id = id;
        this.type = type;
        this.resultMappings = resultMappings;
    }

    public ResultMap resolve() {
        return assistant.addResultMap(this.id, this.type, this.resultMappings);
    }

}
