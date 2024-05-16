/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.events;

import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.UUID;

public class OpponentCallAcceptedEvent extends InComingCallFeedbackEvent {
    public OpponentCallAcceptedEvent(UUID caller, UUID callee) {
        super(caller, callee, PlayerContext.CallFeedback.ACCEPT);
    }
}
