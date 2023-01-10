package com.joker.mybatis.cache;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 因为 MyBatis 中的缓存键不是一个简单的 String ，而是通过多个对象组成。所以 CacheKey 可以理解成将多个对象放在一起，计算其缓存键。
 * MyBatis 对于其 Key 的生成采取规则为：[mappedStatementId + offset + limit + SQL + queryParams + environment]生成一个哈希码
 *
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/9
 */
public class CacheKey implements Cloneable, Serializable {

    private static final long serialVersionUID = 1146682552656046210L;

    /**
     * 单例 - 空缓存键
     */
    public static final CacheKey NULL_CACHE_KEY = new NullCacheKey();

    /**
     * 默认 {@link #multiplier} 的值
     */
    private static final int DEFAULT_MULTIPLYER = 37;
    /**
     * 默认 {@link #hashcode} 的值
     */
    private static final int DEFAULT_HASHCODE = 17;

    /**
     * hashcode 求值的系数
     */
    private final int multiplier;
    /**
     * 缓存键的 hashcode
     */
    private int hashcode;
    /**
     * 校验和
     */
    private long checksum;
    /**
     * {@link #update(Object)} 的数量
     */
    private int count;
// 8/21/2017 - Sonarlint flags this as needing to be marked transient.  While true if content is not serializable, this is not always true and thus should not be marked transient.
    /**
     * 计算 {@link #hashcode} 的对象的集合
     */
    private List<Object> updateList;

    public CacheKey() {
        this.hashcode = DEFAULT_HASHCODE;
        this.multiplier = DEFAULT_MULTIPLYER;
        this.count = 0;
        this.updateList = new ArrayList<>();
    }

    public CacheKey(Object[] objects) {
        this();
        // 基于 objects ，更新相关属性
        updateAll(objects);
    }

    public int getUpdateCount() {
        return updateList.size();
    }

    public void update(Object object) {
        if (object != null && object.getClass().isArray()) {
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(object, i);
                doUpdate(element);
            }
        } else {
            doUpdate(object);
        }
    }

    private void doUpdate(Object object) {
        // 计算Hash值，校验码
        int baseHashCode = object == null ? 1 : object.hashCode();

        count++;
        checksum += baseHashCode;
        baseHashCode *= count;

        hashcode = multiplier * hashcode + baseHashCode;

        // 把对象添加到 updateList，用于 equals 判断
        updateList.add(object);
    }

    /**
     * 更新相关属性
     *
     * @param objects
     */
    public void updateAll(Object[] objects) {
        for (Object o : objects) {
            update(o);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof CacheKey)) {
            return false;
        }

        final CacheKey cacheKey = (CacheKey) object;

        if (hashcode != cacheKey.hashcode) {
            return false;
        }
        if (checksum != cacheKey.checksum) {
            return false;
        }
        if (count != cacheKey.count) {
            return false;
        }

        for (int i = 0; i < updateList.size(); i++) {
            Object thisObject = updateList.get(i);
            Object thatObject = cacheKey.updateList.get(i);
            if (thisObject == null) {
                if (thatObject != null) {
                    return false;
                }
            } else {
                if (!thisObject.equals(thatObject)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hashcode;
    }

    @Override
    public String toString() {
        StringBuilder returnValue = new StringBuilder().append(hashcode).append(':').append(checksum);
        for (Object obj : updateList) {
            returnValue.append(':').append(obj);
        }

        return returnValue.toString();
    }

    @Override
    public CacheKey clone() throws CloneNotSupportedException {
        CacheKey clonedCacheKey = (CacheKey) super.clone();
        // 创建 updateList 数组，避免原数组修改
        clonedCacheKey.updateList = new ArrayList<>(updateList);
        return clonedCacheKey;
    }

}
