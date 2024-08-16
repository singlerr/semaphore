/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.instances.client;

import io.github.singlerr.semaphore.utils.SideUtils;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.fml.relauncher.Side;

public final class ClientResources {

    private static final Map<Class<?>, Object> beans = new HashMap<>();

    private ClientResources() {}

    public static <T> void setInstance(Class<T> beanCls, T bean) {
        SideUtils.validateSide(beanCls, Side.CLIENT);
        if (beans.containsKey(beanCls))
            throw new IllegalStateException("Cannot assign twice of " + beanCls.getSimpleName());
        beans.put(beanCls, bean);
    }

    public static <T> T getInstance(Class<T> beanCls) {
        return (T) beans.get(beanCls);
    }
}
