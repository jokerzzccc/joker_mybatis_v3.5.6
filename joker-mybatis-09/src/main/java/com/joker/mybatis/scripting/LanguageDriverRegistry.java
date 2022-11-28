package com.joker.mybatis.scripting;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * LanguageDriver 注册表:
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/11/23
 */
public class LanguageDriverRegistry {

    /**
     * LanguageDriver 注册表
     */
    private final Map<Class<?>, LanguageDriver> LANGUAGE_DRIVER_MAP = new HashMap<>();

    private Class<?> defaultDriverClass = null;

    public void register(Class<?> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("null is not a valid Language Driver");
        }
        if (!LanguageDriver.class.isAssignableFrom(cls)) {
            throw new RuntimeException(cls.getName() + " does not implements " + LanguageDriver.class.getName());
        }
        LanguageDriver driver = LANGUAGE_DRIVER_MAP.get(cls);
        if (driver == null) {
            try {
                driver = (LanguageDriver) cls.newInstance();
                LANGUAGE_DRIVER_MAP.put(cls, driver);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load language driver for " + cls.getName(), e);
            }
        }
    }

    public LanguageDriver getDriver(Class<?> cls) {
        return LANGUAGE_DRIVER_MAP.get(cls);
    }

    public LanguageDriver getDefaultDriver() {
        return getDriver(getDefaultDriverClass());
    }

    public Class<?> getDefaultDriverClass() {
        return defaultDriverClass;
    }

    /**
     * 设置 {@link #defaultDriverClass}
     * Configuration()有调用，默认的为XMLLanguageDriver
     *
     * @param defaultDriverClass
     */
    public void setDefaultDriverClass(Class<?> defaultDriverClass) {
        register(defaultDriverClass);
        this.defaultDriverClass = defaultDriverClass;
    }

}
