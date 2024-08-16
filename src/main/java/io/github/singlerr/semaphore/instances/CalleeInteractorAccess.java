/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.instances;

import io.github.singlerr.semaphore.interactors.callee.CalleeInteractor;

public final class CalleeInteractorAccess {

    private CalleeInteractorAccess() {}

    private static CalleeInteractor instance;

    public static void setInstance(CalleeInteractor instance) {
        if (CalleeInteractorAccess.instance != null) throw new IllegalStateException("Cannot assign twice");

        CalleeInteractorAccess.instance = instance;
    }

    public static CalleeInteractor getInstance() {
        return instance;
    }
}
