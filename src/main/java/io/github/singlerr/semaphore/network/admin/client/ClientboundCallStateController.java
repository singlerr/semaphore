/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.client;

import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.CallStateQuery;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.admin.packet.PacketCloseCall;
import io.github.singlerr.semaphore.network.admin.packet.PacketCloseCallById;
import io.github.singlerr.semaphore.network.admin.packet.PacketOpenCall;

public final class ClientboundCallStateController implements CallStateController {

    private final NetworkManager networkManager;

    public ClientboundCallStateController(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void openCall(CallStateQuery.OpenCall query) {
        this.networkManager.sendToServer(new PacketOpenCall(query.getCallerId(), query.getCalleeId()));
    }

    @Override
    public void closeCall(CallStateQuery.CloseCall query) {
        this.networkManager.sendToServer(new PacketCloseCall(query.getCallerId(), query.getCalleeId()));
    }

    @Override
    public void closeCall(CallStateQuery.CloseCallById query) {
        this.networkManager.sendToServer(new PacketCloseCallById(query.getId()));
    }
}
