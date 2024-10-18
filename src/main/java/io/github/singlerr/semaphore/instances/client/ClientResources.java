/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.instances.client;

import de.maxhenkel.voicechat.api.VoicechatClientApi;
import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.utils.SideUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;

public final class ClientResources {

    private static final Map<Class<?>, Object> beans = new HashMap<>();

    private ClientResources() {
    }

    public static <T> void setInstance(Class<T> beanCls, T bean) {
        SideUtils.validateSide(beanCls, Side.CLIENT);
        if (beans.containsKey(beanCls))
            throw new IllegalStateException("Cannot assign twice of " + beanCls.getSimpleName());
        beans.put(beanCls, bean);
    }

    public static <T> T getInstance(Class<T> beanCls) {
        return (T) beans.get(beanCls);
    }

    public static final class StaticResources {

        public static final SoundEvent SOUND_RECEIVING_CALL =
                new SoundEvent(new ResourceLocation(Semaphore.MOD_ID, "sound_requesting_call"));
        public static VoicechatClientApi VOICECHAT_CLIENT_API;

        private StaticResources() {
        }
    }
}
