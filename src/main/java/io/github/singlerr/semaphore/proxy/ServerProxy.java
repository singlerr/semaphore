/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.proxy;

import io.github.singlerr.semaphore.commands.CommandManagement;
import io.github.singlerr.semaphore.regisries.ServerRegistries;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ServerRegistries.apply(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ServerRegistries.apply(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        ServerRegistries.apply(event);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandManagement());
    }
}
