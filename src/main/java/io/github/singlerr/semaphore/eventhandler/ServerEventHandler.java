/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.eventhandler;

import io.github.singlerr.semaphore.network.packets.PlayerStatePacket;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// TODO("Test server only. Remove this when publishing")
@SideOnly(Side.SERVER)
public class ServerEventHandler {

    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(50);

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        SERVICE.schedule(
                () -> {
                    CommonRegistries.NETWORK.sendToAll(PlayerStatePacket.builder()
                            .id(event.player.getUniqueID())
                            .state(PlayerContext.builder()
                                    .owner(event.player.getUniqueID())
                                    .name(event.player.getName())
                                    .callState(PlayerContext.CallState.IDLE)
                                    .build())
                            .build());
                },
                1000,
                TimeUnit.MILLISECONDS);
    }
}
