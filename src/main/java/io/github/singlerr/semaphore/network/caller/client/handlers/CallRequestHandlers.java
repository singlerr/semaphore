package io.github.singlerr.semaphore.network.caller.client.handlers;

import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.network.ClientboundPacketHandler;
import io.github.singlerr.semaphore.network.caller.packet.PacketInverseCallRequest;

public final class CallRequestHandlers {

    private CallRequestHandlers(){}

    public static class InverseCallRequestHandler extends ClientboundPacketHandler<PacketInverseCallRequest, PacketInverseCallRequest>{

        private CallRequestPresenter presenter;

        public InverseCallRequestHandler(){}

        public InverseCallRequestHandler(CallRequestPresenter presenter){
            this.presenter = presenter;
        }

        @Override
        public PacketInverseCallRequest handleClient(PacketInverseCallRequest packet, ClientboundPacketContext context) {
            this.presenter.present(new InverseCallRequest(packet.getCallerId(), packet.getCalleeId()));
            return null;
        }
    }
}
