/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.proxy;

import io.github.singlerr.semaphore.regisries.CommonRegistries;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        CommonRegistries.apply(event);
    }

    public void init(FMLInitializationEvent event) {
        CommonRegistries.apply(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
        CommonRegistries.apply(event);
    }

    public void serverStarting(FMLServerStartingEvent event) {}
}
