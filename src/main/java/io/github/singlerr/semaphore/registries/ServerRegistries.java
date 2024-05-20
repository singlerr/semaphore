/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.registries;

import io.github.singlerr.semaphore.eventhandler.ServerEventHandler;
import io.github.singlerr.semaphore.state.StatePool;
import io.github.singlerr.semaphore.utils.EventPool;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Log4j2
@NoArgsConstructor(access = AccessLevel.NONE)
public final class ServerRegistries {

    @Getter
    public static final StatePool statePool = new StatePool();

    private static final EventPool eventPool = new EventPool();

    private static final ScheduledExecutorService taskScheduler = Executors.newScheduledThreadPool(50);

    public static EventPool getEventPool() {
        return eventPool;
    }

    public static ScheduledExecutorService getTaskScheduler() {
        return taskScheduler;
    }

    public static void apply(FMLPreInitializationEvent event) {}

    public static void apply(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }

    public static void apply(FMLPostInitializationEvent event) {}
}
