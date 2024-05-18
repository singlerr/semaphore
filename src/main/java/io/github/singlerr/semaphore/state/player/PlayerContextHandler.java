/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.state.player;

import io.github.singlerr.semaphore.events.*;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.utils.EventPool;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class PlayerContextHandler {

    private EventPool eventPool;

    public void register(EventPool eventPool) {
        PlayerContextHandler.eventPool = eventPool;
        eventPool.subscribe(PlayerStateChangeEvent.class, PlayerContextHandler::onPlayerStateChange);
        eventPool.subscribe(ReceivingCallEvent.class, PlayerContextHandler::onReceivingCall);
        eventPool.subscribe(SendingCallEvent.class, PlayerContextHandler::onSendingCall);
        eventPool.subscribe(InComingCallFeedbackEvent.class, PlayerContextHandler::onInComingCallFeedback);
        eventPool.subscribe(OutGoingCallFeedbackEvent.class, PlayerContextHandler::onOutComingCallFeedback);
        eventPool.subscribe(CallClosedEvent.class, PlayerContextHandler::onCallClosed);
        eventPool.subscribe(RemovePlayerStateEvent.class, PlayerContextHandler::onPlayerStateRemove);
    }

    private void onPlayerStateChange(PlayerStateChangeEvent event) {
        CommonRegistries.getStatePool().submit(event.getState().getOwner(), event.getState());
    }

    private void onPlayerStateRemove(RemovePlayerStateEvent event) {
        CommonRegistries.getStatePool().remove(event.getId());
    }

    private void onReceivingCall(ReceivingCallEvent event) {
        Optional<PlayerContext> ctx = CommonRegistries.getStatePool().get(event.getCallee(), PlayerContext.class);
        ctx.ifPresent(context -> {
            if (context.getCallState() != PlayerContext.CallState.IDLE) {
                context.getOrCreateMissCall(event.getCaller()).incrementAndGet();
                eventPool.invoke(new OutGoingCallFeedbackEvent(
                        event.getCaller(), event.getCallee(), PlayerContext.CallFeedback.DENY_IN_CALL));
                return;
            }

            context.setOpponent(event.getCaller());
            context.setCallState(PlayerContext.CallState.RECEIVING_CALL);
            eventPool.invoke(new PlayerStateChangeEvent(context));
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
                eventPool.invoke(new PlayerStateChangeEvent(playerContext));
                return;
            }
            playerContext.setOpponent(event.getCallee());
            playerContext.setCallState(PlayerContext.CallState.IN_CALL);
            eventPool.invoke(new PlayerStateChangeEvent(playerContext));
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

            eventPool.invoke(new PlayerStateChangeEvent(playerContext));
        });
        ctx = CommonRegistries.getStatePool().get(event.getCaller(), PlayerContext.class);
        ctx.ifPresent(playerContext -> {
            if (event.getFeedback() == PlayerContext.CallFeedback.ACCEPT) {
                playerContext.setCallState(PlayerContext.CallState.IN_CALL);
                playerContext.setOpponent(event.getCaller());
            } else {
                playerContext.setCallState(PlayerContext.CallState.IDLE);
                playerContext.setOpponent(PlayerContext.NULL);
            }
            eventPool.invoke(new PlayerStateChangeEvent(playerContext));
        });
    }

    private void onCallClosed(CallClosedEvent event) {
        Optional<PlayerContext> ctx = CommonRegistries.getStatePool().get(event.getCallee(), PlayerContext.class);
        ctx.ifPresent(playerContext -> {
            playerContext.setCallState(PlayerContext.CallState.IDLE);
            playerContext.setOpponent(PlayerContext.NULL);
            eventPool.invoke(new PlayerStateChangeEvent(playerContext));
        });

        ctx = CommonRegistries.getStatePool().get(event.getCaller(), PlayerContext.class);
        ctx.ifPresent(playerContext -> {
            playerContext.setCallState(PlayerContext.CallState.IDLE);
            playerContext.setOpponent(PlayerContext.NULL);
            eventPool.invoke(new PlayerStateChangeEvent(playerContext));
        });
    }
}
