/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.instances;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;

public final class DatabaseAccess {

    private DatabaseAccess() {}

    private static DatabaseGateway instance;

    public static void setInstance(DatabaseGateway instance) {
        if (DatabaseAccess.instance != null)
            throw new IllegalStateException("Cannot assign AdminInteractor after assigned!");

        DatabaseAccess.instance = instance;
    }

    public static DatabaseGateway getInstance() {
        return instance;
    }
}
