/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.eventhandler;

import io.github.singlerr.semaphore.network.packets.InitializePlayerStatePacket;
import io.github.singlerr.semaphore.network.packets.UpdatePlayerStatePacket;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.registries.ServerRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.NetworkUtils;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

// TODO("Test server only. Remove this when publishing")
@SideOnly(Side.SERVER)
public class ServerEventHandler {

    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(50);

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        FMLServerHandler.instance()
                .getServer()
                .getPlayerList()
                .sendMessage(new TextComponentString(
                        "Synchronizing all player states to " + event.player.getName())
                        .setStyle(new Style().setColor(TextFormatting.AQUA)));
        SERVICE.schedule(
                () -> {
                    PlayerContext newCtx = PlayerContext.builder()
                            .owner(event.player.getUniqueID())
                            .name(event.player.getName())
                            .build();
                    ServerRegistries.getStatePool().submit(event.player.getUniqueID(), newCtx);
                    List<PlayerContext> contexts = ServerRegistries.getStatePool().getStates().stream()
                            .filter(s -> s.getValue() instanceof PlayerContext
                                    && !((PlayerContext) (s.getValue()))
                                            .getOwner()
                                            .equals(newCtx.getOwner()))
                            .map(s -> (PlayerContext) s.getValue())
                            .collect(Collectors.toList());
                    CommonRegistries.NETWORK.sendTo(
                            InitializePlayerStatePacket.builder()
                                    .contexts(contexts)
                                    .build(),
                            (EntityPlayerMP) event.player);

                    NetworkUtils.sendToIgnoreSender(
                            CommonRegistries.NETWORK,
                            (EntityPlayerMP) event.player,
                            UpdatePlayerStatePacket.builder()
                                    .playerState(newCtx)
                                    .build());

                    FMLServerHandler.instance()
                            .getServer()
                            .getPlayerList()
                            .sendMessage(new TextComponentString("Synchronization ended without errors")
                                    .setStyle(new Style().setColor(TextFormatting.AQUA)));
                },
                700,
                TimeUnit.MILLISECONDS);
    }
}
