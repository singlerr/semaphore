/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.events;

import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.UUID;

public class OutComingCallFeedbackEvent extends CallFeedbackEvent {

    public OutComingCallFeedbackEvent(UUID caller, UUID callee, PlayerContext.CallFeedback feedback) {
        super(caller, callee, feedback);
    }
}
