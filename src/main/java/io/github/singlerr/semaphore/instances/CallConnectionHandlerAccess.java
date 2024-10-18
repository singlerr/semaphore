/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.instances;

import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;

public final class CallConnectionHandlerAccess {

    private static CallConnectionHandler instance;

    private CallConnectionHandlerAccess() {
    }

    public static CallConnectionHandler getInstance() {
        return instance;
    }

    public static void setInstance(CallConnectionHandler instance) {
        if (CallConnectionHandlerAccess.instance != null) throw new IllegalStateException("Cannot assign twice");

        CallConnectionHandlerAccess.instance = instance;
    }
}
