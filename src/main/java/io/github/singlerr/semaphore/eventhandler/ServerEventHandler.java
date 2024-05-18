/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.eventhandler;

import io.github.singlerr.semaphore.events.PlayerStateChangeEvent;
import io.github.singlerr.semaphore.network.packets.PlayerStatePacket;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.registries.ServerRegistries;
import io.github.singlerr.semaphore.state.State;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
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
                    ServerRegistries.getEventPool()
                            .invoke(new PlayerStateChangeEvent(PlayerContext.builder()
                                    .owner(event.player.getUniqueID())
                                    .name(event.player.getName())
                                    .callState(PlayerContext.CallState.IDLE)
                                    .build()));

                    for (Map.Entry<UUID, State<?>> entry :
                            CommonRegistries.getStatePool().getStates()) {
                        State<?> state = entry.getValue();
                        if (!(state instanceof PlayerContext)) continue;
                        PlayerContext ctx = (PlayerContext) state;

                        if (ctx.getOwner().equals(event.player.getUniqueID())) {
                            continue;
                        }

                        CommonRegistries.NETWORK.sendTo(
                                PlayerStatePacket.builder()
                                        .id(ctx.getOwner())
                                        .state(ctx)
                                        .build(),
                                (EntityPlayerMP) event.player);
                    }
                },
                1000,
                TimeUnit.MILLISECONDS);
    }
}
