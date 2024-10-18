/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.instances;

import io.github.singlerr.semaphore.interactors.caller.CallerInteractor;

public final class CallerInteractorAccess {

    private static CallerInteractor instance;

    private CallerInteractorAccess() {
    }

    public static CallerInteractor getInstance() {
        return instance;
    }

    public static void setInstance(CallerInteractor instance) {
        if (CallerInteractorAccess.instance != null) throw new IllegalStateException("Cannot assign twice");

        CallerInteractorAccess.instance = instance;
    }
}
