/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.state.player;

import io.github.singlerr.semaphore.events.*;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.utils.EventPool;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class PlayerContextHandler {

    public void register(EventPool eventPool) {
        eventPool.subscribe(PlayerStateChangeEvent.class, PlayerContextHandler::updatePlayerState);
        eventPool.subscribe(ReceivingCallEvent.class, PlayerContextHandler::onReceivingCall);
        eventPool.subscribe(SendingCallEvent.class, PlayerContextHandler::onSendingCall);
        eventPool.subscribe(InComingCallFeedbackEvent.class, PlayerContextHandler::onInComingCallFeedback);
        eventPool.subscribe(OutGoingCallFeedbackEvent.class, PlayerContextHandler::onOutComingCallFeedback);
        eventPool.subscribe(CallClosedEvent.class, PlayerContextHandler::onCallClosed);
    }

    private void updatePlayerState(PlayerStateChangeEvent event) {
        CommonRegistries.getStatePool().submit(event.getState().getOwner(), event.getState());
    }

    private void onReceivingCall(ReceivingCallEvent event) {
        Optional<PlayerContext> ctx = CommonRegistries.getStatePool().get(event.getCallee(), PlayerContext.class);
        ctx.ifPresent(context -> {
            if (context.getCallState() != PlayerContext.CallState.IDLE) {
                context.getOrCreateMissCall(event.getCaller()).incrementAndGet();
                return;
            }

            context.setOpponent(event.getCaller());
            context.setCallState(PlayerContext.CallState.RECEIVING_CALL);
        });
    }

    private void onSendingCall(SendingCallEvent event) {
        Optional<PlayerContext> ctx = CommonRegistries.getStatePool().get(event.getCaller(), PlayerContext.class);
        ctx.ifPresent(playerContext -> {
            playerContext.getOrCreateMissCall(event.getCallee()).set(0);
        });
    }

    private void onInComingCallFeedback(InComingCallFeedbackEvent event) {
        Optional<PlayerContext> ctx = CommonRegistries.getStatePool().get(event.getCaller(), PlayerContext.class);
        ctx.ifPresent(playerContext -> {
            if (event.getFeedback() != PlayerContext.CallFeedback.ACCEPT) {
                playerContext.setCallState(PlayerContext.CallState.IDLE);
                return;
            }
            playerContext.setOpponent(event.getCallee());
            playerContext.setCallState(PlayerContext.CallState.IN_CALL);
        });
    }

    private void onOutComingCallFeedback(OutGoingCallFeedbackEvent event) {
        Optional<PlayerContext> ctx = CommonRegistries.getStatePool().get(event.getCallee(), PlayerContext.class);
        ctx.ifPresent(playerContext -> {
            if (event.getFeedback() == PlayerContext.CallFeedback.ACCEPT) {
                playerContext.setCallState(PlayerContext.CallState.IN_CALL);
                playerContext.setOpponent(event.getCaller());
            } else {
                playerContext.setCallState(PlayerContext.CallState.IDLE);
                playerContext.setOpponent(PlayerContext.NULL);
            }
        });
    }

    private void onCallClosed(CallClosedEvent event) {
        Optional<PlayerContext> ctx = CommonRegistries.getStatePool().get(event.getCallee(), PlayerContext.class);
        ctx.ifPresent(playerContext -> {
            playerContext.setCallState(PlayerContext.CallState.IDLE);
            playerContext.setOpponent(PlayerContext.NULL);
        });

        ctx = CommonRegistries.getStatePool().get(event.getCaller(), PlayerContext.class);
        ctx.ifPresent(playerContext -> {
            playerContext.setCallState(PlayerContext.CallState.IDLE);
            playerContext.setOpponent(PlayerContext.NULL);
        });
    }
}
