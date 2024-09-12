/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.callee.client;

import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController;
import io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.callee.packet.PacketCallResponse;

public final class ClientboundCallResponseController implements CallResponseController {

    private final NetworkManager networkManager;

    public ClientboundCallResponseController(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void reply(CallResponse response) {
        this.networkManager.sendToServer(
                new PacketCallResponse(response.getCallerId(), response.getCalleeId(), response.getResponse()));
    }
}
