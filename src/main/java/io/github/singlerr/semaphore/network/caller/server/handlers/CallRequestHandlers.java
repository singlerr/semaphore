package io.github.singlerr.semaphore.network.caller.server.handlers;

import io.github.singlerr.semaphore.interactors.caller.controller.data.CallRequest;
import io.github.singlerr.semaphore.network.ServerboundPacketHandler;
import io.github.singlerr.semaphore.network.caller.packet.PacketCallRequest;
import io.github.singlerr.semaphore.network.caller.server.ServerboundCallRequestController;

public final class CallRequestHandlers {

    private CallRequestHandlers(){}

    public static class CallRequestHandler extends ServerboundPacketHandler<PacketCallRequest, PacketCallRequest> {

        private final ServerboundCallRequestController requestController;

        public CallRequestHandler(){
            throw new IllegalStateException();
        }

        public CallRequestHandler(ServerboundCallRequestController requestController){
            this.requestController = requestController;
        }

        @Override
        public PacketCallRequest handleServer(PacketCallRequest packet, ServerboundPacketContext context) {
            this.requestController.request(new CallRequest(packet.getCallerId(), packet.getCalleeId()));
            return null;
        }
    }
}
