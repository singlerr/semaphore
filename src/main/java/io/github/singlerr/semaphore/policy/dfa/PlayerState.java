/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.dfa;

public enum PlayerState {
    DEFAULT,

    REQUESTING_CALL,
    RECEIVING_CALL,
    IN_CALL
}
