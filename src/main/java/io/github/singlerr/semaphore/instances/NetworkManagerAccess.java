/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.instances;

import io.github.singlerr.semaphore.network.NetworkManager;

public final class NetworkManagerAccess {

    private NetworkManagerAccess() {}

    private static NetworkManager instance;

    public static void setInstance(NetworkManager instance) {
        if (NetworkManagerAccess.instance != null) throw new IllegalStateException("Cannot assign twice");

        NetworkManagerAccess.instance = instance;
    }

    public static NetworkManager getInstance() {
        return instance;
    }
}
