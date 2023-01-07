package com.joker.mybatis.reflection.property;

import java.util.Iterator;

/**
 * <p>
 * 属性分词器: 支持迭代器的访问方式
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/29
 */
public class PropertyTokenizer implements Iterable<PropertyTokenizer>, Iterator<PropertyTokenizer> {


    /**
     * 以下属性都已这个为例：例子：班级[0].学生.成绩
     * 当前字符串。
     * 班级
     */
    private String name;
    /**
     * 索引的 {@link #name} ，因为 {@link #name} 如果存在 {@link #index} 会被更改
     * 班级[0]
     */
    private final String indexedName;
    /**
     * 编号。
     * 0
     *
     * 对于数组 name[0] ，则 index = 0
     * 对于 Map map[key] ，则 index = key
     */
    private String index;
    /**
     * 剩余字符串
     * 学生.成绩
     */
    private final String children;


    public PropertyTokenizer(String fullname) {
        int delim = fullname.indexOf('.');
        if (delim > -1) {
            name = fullname.substring(0, delim);
            children = fullname.substring(delim + 1);
        } else {
            name = fullname;
            children = null;
        }
        indexedName = name;
        delim = name.indexOf('[');
        if (delim > -1) {
            index = name.substring(delim + 1, name.length() - 1);
            name = name.substring(0, delim);
        }
    }


    public String getName() {
        return name;
    }

    public String getIndex() {
        return index;
    }

    public String getIndexedName() {
        return indexedName;
    }

    public String getChildren() {
        return children;
    }

    @Override
    public boolean hasNext() {
        return children != null;
    }

    // 取得下一个,非常简单，直接再通过儿子来new另外一个实例
    @Override
    public PropertyTokenizer next() {
        return new PropertyTokenizer(children);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported, as it has no meaning in the context of properties.");
    }

    @Override
    public Iterator<PropertyTokenizer> iterator() {
        return this;
    }



}
