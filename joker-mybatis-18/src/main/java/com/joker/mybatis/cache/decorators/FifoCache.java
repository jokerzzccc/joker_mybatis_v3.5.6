package com.joker.mybatis.cache.decorators;

import com.joker.mybatis.cache.Cache;

import java.util.Deque;
import java.util.LinkedList;

/**
 * <p>
 * FIFO (first in, first out) cache decorator:
 * 基于先进先出的淘汰机制的 Cache 实现类。
 * FIFO先进先出队列，基于Deque维护了一个链表，其他的操作都包装给Cache去完成，属于典型的装饰器模式。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/10
 */
public class FifoCache implements Cache {

    /**
     * 装饰的 Cache 对象
     */
    private final Cache delegate;
    /**
     * 双端队列，记录缓存键的添加:
     */
    private final Deque<Object> keyList;
    /**
     * 队列上限
     */
    private int size;

    public FifoCache(Cache delegate) {
        this.delegate = delegate;
        this.keyList = new LinkedList<>();
        this.size = 1024;
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public void putObject(Object key, Object value) {
        cycleKeyList(key);
        delegate.putObject(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return delegate.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        delegate.clear();
        keyList.clear();
    }

    /**
     * 作用是在增加记录时判断记录是否超过 size 值，以此移除链表的第一个元素，从而达到 FIFO 缓存效果。
     */
    private void cycleKeyList(Object key) {
        keyList.addLast(key);
        if (keyList.size() > size) {
            Object oldestKey = keyList.removeFirst();
            delegate.removeObject(oldestKey);
        }
    }

}
