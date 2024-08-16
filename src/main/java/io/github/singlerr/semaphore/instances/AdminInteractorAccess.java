/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.instances;

import io.github.singlerr.semaphore.interactors.admin.AdminInteractor;

public final class AdminInteractorAccess {

    private AdminInteractorAccess() {}

    private static AdminInteractor instance;

    public static void setInstance(AdminInteractor instance) {
        if (AdminInteractorAccess.instance != null)
            throw new IllegalStateException("Cannot assign twice");

        AdminInteractorAccess.instance = instance;
    }

    public static AdminInteractor getInstance() {
        return instance;
    }
}
