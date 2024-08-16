/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.client;

import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.CallStateQuery;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.admin.packet.PacketGetCallState;
import io.github.singlerr.semaphore.network.admin.packet.PacketSetCallState;

public final class ClientboundCallStateController implements CallStateController {

    private final NetworkManager networkManager;

    public ClientboundCallStateController(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void getCallState(CallStateQuery.GetCallState query) {
        this.networkManager.sendToServer(new PacketGetCallState(query.id()));
    }

    @Override
    public void setCallState(CallStateQuery.SetCallState query) {
        this.networkManager.sendToServer(new PacketSetCallState(query.id(), query.state()));
    }
}
