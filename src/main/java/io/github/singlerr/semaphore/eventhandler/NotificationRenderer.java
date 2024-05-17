/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.eventhandler;

import io.github.singlerr.semaphore.events.OutGoingCallFeedbackEvent;
import io.github.singlerr.semaphore.gui.NotificationWindow;
import io.github.singlerr.semaphore.gui.PhoneScreen;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.Optional;
import java.util.UUID;
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

        UUID playerId = Minecraft.getMinecraft().getSession().getProfile().getId();
        Optional<PlayerContext> ctx = CommonRegistries.getStatePool().get(playerId, PlayerContext.class);

        if (!ctx.isPresent()) return;

        PlayerContext context = ctx.get();
        if (context.getCallState() == PlayerContext.CallState.RECEIVING_CALL
                && context.getOpponent() != PlayerContext.NULL) {
            NotificationWindow window = ClientRegistries.getOrCreate(context.getOpponent());
            window.draw();
        }
    }

    @SubscribeEvent
    public void handleInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getMinecraft().currentScreen instanceof PhoneScreen) {
            return;
        }
        UUID playerId = Minecraft.getMinecraft().getSession().getProfile().getId();
        Optional<PlayerContext> ctx = CommonRegistries.getStatePool().get(playerId, PlayerContext.class);

        if (!ctx.isPresent()) return;
        PlayerContext context = ctx.get();
        if (context.getCallState() == PlayerContext.CallState.RECEIVING_CALL
                && context.getOpponent() != PlayerContext.NULL) {
            NotificationWindow window = ClientRegistries.getOrCreate(context.getOpponent());
            if (ClientRegistries.KEY_ACCEPT_CALL.isPressed()) {
                ClientRegistries.getEventPool()
                        .invoke(new OutGoingCallFeedbackEvent(
                                context.getOpponent(), context.getOwner(), PlayerContext.CallFeedback.ACCEPT));
                return;
            }
            if (ClientRegistries.KEY_DENY_CALL.isPressed()) {
                ClientRegistries.getEventPool()
                        .invoke(new OutGoingCallFeedbackEvent(
                                context.getOpponent(),
                                context.getOwner(),
                                PlayerContext.CallFeedback.DENY_NOT_AVAILABLE));
                return;
            }
        }
    }
}
