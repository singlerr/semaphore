/* (C) 2024 singlerr */
package io.github.singlerr.semaphore;

import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import io.github.singlerr.semaphore.proxy.CommonProxy;
import io.github.singlerr.semaphore.proxy.ServerProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Semaphore.MOD_ID, dependencies = Semaphore.MOD_DEPENDENCIES)
public class Semaphore {

    public static final String MOD_DEPENDENCIES = "required-after:voicechat";

    public static final String MOD_ID = "semaphore";

    @SidedProxy(
            clientSide = "io.github.singlerr.semaphore.proxy.ClientProxy",
            serverSide = "io.github.singlerr.semaphore.proxy.ServerProxy",
            modId = MOD_ID)
    private static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {}

    public static void serverStarted(VoicechatServerStartedEvent event){
        if(proxy instanceof ServerProxy){
            ((ServerProxy) proxy).serverStarted(event);
        }
    }

    public static CommonProxy getProxy() {
        return proxy;
    }
}
