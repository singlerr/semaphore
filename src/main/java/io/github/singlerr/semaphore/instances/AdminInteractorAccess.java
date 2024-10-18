/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.instances;

import io.github.singlerr.semaphore.interactors.admin.AdminInteractor;

public final class AdminInteractorAccess {

    private static AdminInteractor instance;

    private AdminInteractorAccess() {
    }

    public static AdminInteractor getInstance() {
        return instance;
    }

    public static void setInstance(AdminInteractor instance) {
        if (AdminInteractorAccess.instance != null) throw new IllegalStateException("Cannot assign twice");

        AdminInteractorAccess.instance = instance;
    }
}
