/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.caller.client;

import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController;
import io.github.singlerr.semaphore.interactors.caller.controller.data.CallRequest;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.caller.packet.PacketCallRequest;

public final class ClientboundCallRequestController implements CallRequestController {

    private final NetworkManager networkManager;

    public ClientboundCallRequestController(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void request(CallRequest request) {
        this.networkManager.sendToServer(new PacketCallRequest(request.getCallerId(), request.getCalleeId()));
    }
}
