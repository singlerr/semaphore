/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.eventhandler;

import gg.essential.universal.UMatrixStack;
import io.github.singlerr.semaphore.gui.NotificationWindow;
import io.github.singlerr.semaphore.gui.PhoneScreen;
import io.github.singlerr.semaphore.network.packets.CallAcceptPacket;
import io.github.singlerr.semaphore.network.packets.CallRejectPacket;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.sound.ClientSoundHandler;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class NotificationRenderer {

    @SubscribeEvent
    public void render(RenderGameOverlayEvent event) {
        if (Minecraft.getMinecraft().currentScreen instanceof PhoneScreen) {
            return;
        }
        PlayerContext context = ClientRegistries.getPlayerState();

        if (context.getCallState() == PlayerContext.CallState.RECEIVING_CALL
                && context.getOpponent() != PlayerContext.NULL) {
            NotificationWindow window = ClientRegistries.getOrCreateNotificationWindow(context.getOpponent());
            window.getHandle().draw(new UMatrixStack());
        }
    }

    @SubscribeEvent
    public void handleInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getMinecraft().currentScreen instanceof PhoneScreen) {
            return;
        }
        PlayerContext context = ClientRegistries.getPlayerState();

        if (context.getCallState() == PlayerContext.CallState.RECEIVING_CALL) {
            NotificationWindow window = ClientRegistries.getOrCreateNotificationWindow(context.getOpponent());
            if (ClientRegistries.KEY_ACCEPT_CALL.isPressed()) {
                CommonRegistries.NETWORK.sendToServer(CallAcceptPacket.builder()
                        .caller(PlayerContext.from(context.getOpponent()))
                        .callee(context)
                        .build());
                ClientSoundHandler.stopReceivingCallSound();
                window.onHide(() -> {
                    context.setCallState(PlayerContext.CallState.IDLE);
                });
                ClientRegistries.getPhoneScreen().callClosed();
                return;
            }
            if (ClientRegistries.KEY_DENY_CALL.isPressed()) {
                CommonRegistries.NETWORK.sendToServer(CallRejectPacket.builder()
                        .caller(PlayerContext.from(context.getOpponent()))
                        .reason(PlayerContext.CallRejectReason.PLAYER_REJECTED)
                        .callee(context)
                        .build());
                ClientSoundHandler.stopReceivingCallSound();
                window.onHide(() -> {
                    context.setCallState(PlayerContext.CallState.IDLE);
                });
                ClientRegistries.getPhoneScreen().callClosed();
            }
        }
    }
}
