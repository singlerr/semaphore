/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.instances;

import io.github.singlerr.semaphore.network.NetworkManager;

public final class NetworkManagerAccess {

    private static NetworkManager instance;

    private NetworkManagerAccess() {
    }

    public static NetworkManager getInstance() {
        return instance;
    }

    public static void setInstance(NetworkManager instance) {
        if (NetworkManagerAccess.instance != null) throw new IllegalStateException("Cannot assign twice");

        NetworkManagerAccess.instance = instance;
    }
}
