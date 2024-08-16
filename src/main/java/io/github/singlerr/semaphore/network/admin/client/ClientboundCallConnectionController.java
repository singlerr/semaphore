/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.client;

import io.github.singlerr.semaphore.interactors.admin.controller.CallConnectionController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.CallConnectionQuery;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.admin.packet.PacketCloseConnection;
import io.github.singlerr.semaphore.network.admin.packet.PacketGetConnection;
import io.github.singlerr.semaphore.network.admin.packet.PacketOpenConnection;

public final class ClientboundCallConnectionController implements CallConnectionController {

    private final NetworkManager networkManager;

    public ClientboundCallConnectionController(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void openConnection(CallConnectionQuery.OpenConnection query) {
        this.networkManager.sendToServer(new PacketOpenConnection(query.callerId(), query.calleeId()));
    }

    @Override
    public void closeConnection(CallConnectionQuery.CloseConnection closeConnection) {
        this.networkManager.sendToServer(new PacketCloseConnection(closeConnection.id()));
    }

    @Override
    public void getConnection(CallConnectionQuery.GetConnection getConnection) {
        this.networkManager.sendToServer(new PacketGetConnection(getConnection.id()));
    }
}
