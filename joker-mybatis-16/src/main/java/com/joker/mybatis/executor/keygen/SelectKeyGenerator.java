package com.joker.mybatis.executor.keygen;

import com.joker.mybatis.executor.Executor;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.reflection.MetaObject;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.session.RowBounds;

import java.sql.Statement;
import java.util.List;

/**
 * <p>
 * 实现 KeyGenerator 接口，基于从数据库查询主键的 KeyGenerator 实现类，
 * 适用于 Oracle、PostgreSQL 。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/7
 */
public class SelectKeyGenerator implements KeyGenerator {

    public static final String SELECT_KEY_SUFFIX = "!selectKey";
    /**
     * 是否在 before 阶段执行
     * <p>
     * true ：before;
     * false ：after
     */
    private final boolean executeBefore;
    /**
     * MappedStatement 对象
     */
    private final MappedStatement keyStatement;

    public SelectKeyGenerator(MappedStatement keyStatement, boolean executeBefore) {
        this.executeBefore = executeBefore;
        this.keyStatement = keyStatement;
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        if (executeBefore) {
            processGeneratedKeys(executor, ms, parameter);
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        if (!executeBefore) {
            processGeneratedKeys(executor, ms, parameter);
        }
    }

    private void processGeneratedKeys(Executor executor, MappedStatement ms, Object parameter) {
        try {
            // <1> 有查询主键的 SQL 语句，即 keyStatement 对象非空
            if (parameter != null && keyStatement != null && keyStatement.getKeyProperties() != null) {
                String[] keyProperties = keyStatement.getKeyProperties();
                final Configuration configuration = ms.getConfiguration();
                final MetaObject metaParam = configuration.newMetaObject(parameter);
                if (keyProperties != null) {
                    // <2> 创建执行器，类型为 SimpleExecutor
                    Executor keyExecutor = configuration.newExecutor(executor.getTransaction());
                    // <3> 执行查询主键的操作
                    List<Object> values = keyExecutor.query(keyStatement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
                    if (values.size() == 0) {
                        throw new RuntimeException("SelectKey returned no data.");
                    } else if (values.size() > 1) {
                        throw new RuntimeException("SelectKey returned more than one value.");
                    } else {
                        // 创建 MetaObject 对象，访问查询主键的结果
                        MetaObject metaResult = configuration.newMetaObject(values.get(0));
                        // 单个主键
                        if (keyProperties.length == 1) {
                            // 设置属性到 metaParam 中，相当于设置到 parameter 中
                            if (metaResult.hasGetter(keyProperties[0])) {
                                setValue(metaParam, keyProperties[0], metaResult.getValue(keyProperties[0]));
                            } else {
                                setValue(metaParam, keyProperties[0], values.get(0));
                            }
                        }
                        // 多个主键
                        else {
                            // 遍历，进行赋值
                            handleMultipleProperties(keyProperties, metaParam, metaResult);
                        }
                    }

                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error selecting key or setting result to parameter object. Cause: " + e);
        }
    }

    private void handleMultipleProperties(String[] keyProperties,
            MetaObject metaParam, MetaObject metaResult) {
        String[] keyColumns = keyStatement.getKeyColumns();

        if (keyColumns == null || keyColumns.length == 0) {
            for (String keyProperty : keyProperties) {
                setValue(metaParam, keyProperty, metaResult.getValue(keyProperty));
            }
        } else {
            if (keyColumns.length != keyProperties.length) {
                throw new RuntimeException("If SelectKey has key columns, the number must match the number of key properties.");
            }
            for (int i = 0; i < keyProperties.length; i++) {
                setValue(metaParam, keyProperties[i], metaResult.getValue(keyColumns[i]));
            }
        }
    }

    private void setValue(MetaObject metaParam, String property, Object value) {
        if (metaParam.hasSetter(property)) {
            metaParam.setValue(property, value);
        } else {
            throw new RuntimeException("No setter found for the keyProperty '" + property + "' in " + metaParam.getOriginalObject().getClass().getName() + ".");
        }
    }

}
