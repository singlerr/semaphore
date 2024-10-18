/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.instances.common;

import java.util.HashMap;
import java.util.Map;

public final class CommonResources {

    private static final Map<Class<?>, Object> beans = new HashMap<>();

    private CommonResources() {
    }

    public static <T> void setInstance(Class<T> beanCls, T bean) {
        if (beans.containsKey(beanCls))
            throw new IllegalStateException("Cannot assign twice of " + beanCls.getSimpleName());
        beans.put(beanCls, bean);
    }

    public static <T> T getInstance(Class<T> beanCls) {
        return (T) beans.get(beanCls);
    }
}
