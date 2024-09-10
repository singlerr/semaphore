/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.caller.client.handlers;

import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.network.ClientboundPacketHandler;
import io.github.singlerr.semaphore.network.callee.packet.PacketCallResponse;
import io.github.singlerr.semaphore.network.caller.client.ClientboundCallResponsePresenter;

public final class CallResponseHandlers {

    private CallResponseHandlers() {}

    public static class CallResponseHandler extends ClientboundPacketHandler<PacketCallResponse, PacketCallResponse> {

        private ClientboundCallResponsePresenter callResponsePresenter;

        public CallResponseHandler() {}

        public CallResponseHandler(ClientboundCallResponsePresenter callResponsePresenter) {
            this.callResponsePresenter = callResponsePresenter;
        }

        @Override
        public PacketCallResponse handleClient(PacketCallResponse packet, ClientboundPacketContext context) {
            this.callResponsePresenter.present(new CallResponse(
                    packet.getCallerId(),
                    packet.getCalleeId(),
                    packet.getResponse()
                                    == io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse
                                            .Response.ACCEPT
                            ? CallResponse.ResponseType.ACCEPT
                            : CallResponse.ResponseType.REJECT));
            return null;
        }
    }
}
