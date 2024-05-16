/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.events;

import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.UUID;

public class InComingCallFeedbackEvent extends CallFeedbackEvent {

    public InComingCallFeedbackEvent(UUID caller, UUID callee, PlayerContext.CallFeedback feedback) {
        super(caller, callee, feedback);
    }
}
