/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.registries;

import io.github.singlerr.semaphore.config.ModConfig;
import io.github.singlerr.semaphore.eventhandler.ServerEventHandler;
import io.github.singlerr.semaphore.events.CallEvent;
import io.github.singlerr.semaphore.events.CallFeedbackEvent;
import io.github.singlerr.semaphore.events.PlayerStateChangeEvent;
import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.packets.CallActionPacket;
import io.github.singlerr.semaphore.network.packets.CallFeedbackPacket;
import io.github.singlerr.semaphore.network.packets.PlayerStatePacket;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.state.player.PlayerContextHandler;
import io.github.singlerr.semaphore.utils.EventPool;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ServerRegistries {

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
        PlayerContextHandler.register(getEventPool());
        Synchronizer.register(getEventPool());
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }

    public static void apply(FMLPostInitializationEvent event) {}

    @UtilityClass
    private static class Synchronizer {

        public void register(EventPool eventPool) {
            eventPool.subscribe(PlayerStateChangeEvent.class, Synchronizer::handlePlayerStateChange);
            eventPool.subscribe(CallFeedbackEvent.class, Synchronizer::handleCallFeedback);
            eventPool.subscribe(CallEvent.class, Synchronizer::handleCallEvent);
        }

        private void handlePlayerStateChange(PlayerStateChangeEvent event) {
            PlayerStatePacket packet = PlayerStatePacket.builder()
                    .id(event.getState().getOwner())
                    .state(event.getState())
                    .build();

            for (EntityPlayerMP player :
                    FMLServerHandler.instance().getServer().getPlayerList().getPlayers()) {
                if (player.getUniqueID().equals(event.getState().getOwner())) continue;

                CommonRegistries.NETWORK.sendTo(packet, player);
            }
        }

        private void handleCallFeedback(CallFeedbackEvent event) {
            PlayerList playerList = FMLServerHandler.instance().getServer().getPlayerList();
            EntityPlayerMP callerPlayer = playerList.getPlayerByUUID(event.getCaller());
            EntityPlayerMP calleePlayer = playerList.getPlayerByUUID(event.getCallee());

            if (!handlePairPlayerNotAvailable(
                    event.getCaller(),
                    event.getCallee(),
                    CallFeedbackPacket.builder()
                            .callFeedback(PlayerContext.CallFeedback.DENY_NOT_AVAILABLE)
                            .callee(event.getCallee())
                            .caller(event.getCaller())
                            .build())) return;

            CallFeedbackPacket packet = CallFeedbackPacket.builder()
                    .callFeedback(event.getFeedback())
                    .callee(event.getCallee())
                    .caller(event.getCaller())
                    .build();

            CommonRegistries.NETWORK.sendTo(packet, callerPlayer);
            CommonRegistries.NETWORK.sendTo(packet, calleePlayer);
        }

        private boolean handlePairPlayerNotAvailable(UUID caller, UUID callee, Packet nullPacket) {

            PlayerList playerList = FMLServerHandler.instance().getServer().getPlayerList();

            EntityPlayerMP callerPlayer = playerList.getPlayerByUUID(caller);
            EntityPlayerMP calleePlayer = playerList.getPlayerByUUID(callee);

            if (calleePlayer == null || callerPlayer == null) {

                if (callerPlayer != null) CommonRegistries.NETWORK.sendTo(nullPacket, callerPlayer);
                if (calleePlayer != null) CommonRegistries.NETWORK.sendTo(nullPacket, calleePlayer);
                return false;
            }

            return true;
        }

        private void handleCallEvent(CallEvent event) {
            PlayerList playerList = FMLServerHandler.instance().getServer().getPlayerList();
            EntityPlayerMP callerPlayer = playerList.getPlayerByUUID(event.getCaller());
            EntityPlayerMP calleePlayer = playerList.getPlayerByUUID(event.getCallee());

            if (!handlePairPlayerNotAvailable(
                    event.getCaller(),
                    event.getCallee(),
                    CallActionPacket.builder()
                            .caller(event.getCaller())
                            .callee(event.getCallee())
                            .action(PlayerContext.CallAction.CLOSE)
                            .build())) return;

            if (event.getAction() == PlayerContext.CallAction.REQUEST) {
                CommonRegistries.NETWORK.sendTo(
                        CallActionPacket.builder()
                                .callee(event.getCallee())
                                .caller(event.getCaller())
                                .action(PlayerContext.CallAction.REQUEST)
                                .build(),
                        calleePlayer);

                getTaskScheduler()
                        .schedule(
                                () -> {
                                    Optional<PlayerContext> ctx =
                                            CommonRegistries.statePool.get(event.getCaller(), PlayerContext.class);

                                    // Call session successfully established
                                    if (ctx.isPresent() && ctx.get().getCallState() == PlayerContext.CallState.IN_CALL)
                                        return;

                                    CallFeedbackPacket newStatePacket = CallFeedbackPacket.builder()
                                            .callee(event.getCallee())
                                            .caller(event.getCaller())
                                            .callFeedback(PlayerContext.CallFeedback.DENY_NOT_AVAILABLE)
                                            .build();
                                    CommonRegistries.NETWORK.sendTo(newStatePacket, callerPlayer);
                                },
                                (long) (ModConfig.callTimeout * 1000),
                                TimeUnit.MILLISECONDS);
            } else if (event.getAction() == PlayerContext.CallAction.CLOSE) {
                CallActionPacket packet = CallActionPacket.builder()
                        .caller(event.getCaller())
                        .callee(event.getCallee())
                        .action(PlayerContext.CallAction.CLOSE)
                        .build();
                CommonRegistries.NETWORK.sendTo(packet, callerPlayer);
                CommonRegistries.NETWORK.sendTo(packet, calleePlayer);
            }
        }
    }
}
