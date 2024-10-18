/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy;

import io.github.singlerr.semaphore.policy.dfa.NFA;
import io.github.singlerr.semaphore.policy.dfa.PlayerInput;
import io.github.singlerr.semaphore.policy.dfa.PlayerState;

public final class PolicyConstants {

    public static final NFA STATE_NFA = new NFA.Builder()
            .encode(0, PlayerState.DEFAULT)
            .encode(1, PlayerState.REQUESTING_CALL)
            .encode(2, PlayerState.RECEIVING_CALL)
            .encode(3, PlayerState.IN_CALL)
            .transit(0, PlayerInput.REQUEST_CALL, 1)
            .transit(0, PlayerInput.RECEIVE_CALL, 2)
            .transit(1, PlayerInput.CLOSE_CALL, 0)
            .transit(1, PlayerInput.ACCEPT_CALL, 3)
            .transit(2, PlayerInput.REJECT_CALL, 0)
            .transit(2, PlayerInput.ACCEPT_CALL, 3)
            .transit(3, PlayerInput.CLOSE_CALL, 0)
            .transit(1, PlayerInput.CANCEL_CALL, 0)
            .build();

    private PolicyConstants() {
    }
}
