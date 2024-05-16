/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.events;

import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.UUID;

public final class ReceivingCallEvent extends CallEvent {

    public ReceivingCallEvent(UUID callee, UUID caller) {
        super(callee, caller, PlayerContext.CallAction.REQUEST);
    }
}
