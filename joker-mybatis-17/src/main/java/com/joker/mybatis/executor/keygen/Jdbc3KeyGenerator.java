package com.joker.mybatis.executor.keygen;

import com.joker.mybatis.executor.Executor;
import com.joker.mybatis.mapping.MappedStatement;
import com.joker.mybatis.reflection.MetaObject;
import com.joker.mybatis.session.Configuration;
import com.joker.mybatis.type.TypeHandler;
import com.joker.mybatis.type.TypeHandlerRegistry;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * <p>
 * Jdbc3 KeyGenerator:主要用于数据库的自增主键，比如MySQL、PostgreSQL;
 * 使用 JDBC3 Statement.getGeneratedKeys;
 * 对于 Jdbc3KeyGenerator 类的主键，是在 SQL 执行后，才生成。
 * </p>
 *
 * @author jokerzzccc
 * @date 2023/1/7
 */
public class Jdbc3KeyGenerator implements KeyGenerator {

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // do nothing
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

    }

    /**
     * 处理返回的自增主键。单个 parameter 参数，可以认为是批量的一个特例。
     *
     * @param ms
     * @param stmt
     * @param parameter
     */
    private void processBatch(MappedStatement ms, Statement stmt, List<Object> parameters) {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            final Configuration configuration = ms.getConfiguration();
            final TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            final String[] keyProperties = ms.getKeyProperties();
            final ResultSetMetaData rsmd = rs.getMetaData();
            TypeHandler<?>[] typeHandlers = null;
            if (keyProperties != null && rsmd.getColumnCount() >= keyProperties.length) {
                for (Object parameter : parameters) {
                    // there should be one row for each statement (also one for each parameter)
                    if (!rs.next()) {
                        break;
                    }
                    final MetaObject metaParam = configuration.newMetaObject(parameter);
                    if (typeHandlers == null) {
                        // 先取得类型处理器
                        typeHandlers = getTypeHandlers(typeHandlerRegistry, metaParam, keyProperties);
                    }
                    // 填充主键值
                    populateKeys(rs, metaParam, keyProperties, typeHandlers);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
        }
    }

    /**
     * 填充主键
     *
     * @param rs
     * @param metaParam
     * @param keyProperties
     * @param typeHandlers
     * @throws SQLException
     */
    private void populateKeys(ResultSet rs, MetaObject metaParam, String[] keyProperties, TypeHandler<?>[] typeHandlers) throws SQLException {
        for (int i = 0; i < keyProperties.length; i++) {
            TypeHandler<?> th = typeHandlers[i];
            if (th != null) {
                Object value = th.getResult(rs, i + 1);
                metaParam.setValue(keyProperties[i], value);
            }
        }
    }

    /**
     * 获取类型处理器 typeHandler
     *
     * @param typeHandlerRegistry
     * @param metaParam
     * @param keyProperties
     * @return
     */
    private TypeHandler<?>[] getTypeHandlers(TypeHandlerRegistry typeHandlerRegistry, MetaObject metaParam, String[] keyProperties) {
        TypeHandler<?>[] typeHandlers = new TypeHandler<?>[keyProperties.length];
        for (int i = 0; i < keyProperties.length; i++) {
            if (metaParam.hasSetter(keyProperties[i])) {
                Class<?> keyPropertyType = metaParam.getSetterType(keyProperties[i]);
                TypeHandler<?> th = typeHandlerRegistry.getTypeHandler(keyPropertyType, null);
                typeHandlers[i] = th;
            }
        }
        return typeHandlers;
    }

}
