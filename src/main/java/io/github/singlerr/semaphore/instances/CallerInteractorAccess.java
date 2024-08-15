package io.github.singlerr.semaphore.instances;

import io.github.singlerr.semaphore.interactors.callee.CalleeInteractor;
import io.github.singlerr.semaphore.interactors.caller.CallerInteractor;

public final class CallerInteractorAccess {

    private CallerInteractorAccess(){}

    private static CallerInteractor instance;

    public static void setInstance(CallerInteractor instance) {
        if(CallerInteractorAccess.instance != null)
            throw new IllegalStateException("Cannot assign AdminInteractor after assigned!");

        CallerInteractorAccess.instance = instance;
    }

    public static CallerInteractor getInstance() {
        return instance;
    }
}
