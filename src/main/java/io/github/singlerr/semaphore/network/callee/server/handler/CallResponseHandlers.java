/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.callee.server.handler;

import io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse;
import io.github.singlerr.semaphore.network.ServerboundPacketHandler;
import io.github.singlerr.semaphore.network.callee.packet.PacketCallResponse;
import io.github.singlerr.semaphore.network.callee.server.ServerboundCallResponseController;

public final class CallResponseHandlers {

    private CallResponseHandlers() {
    }

    public static class CallResponseHandler extends ServerboundPacketHandler<PacketCallResponse, PacketCallResponse> {

        private ServerboundCallResponseController callResponseController;

        public CallResponseHandler() {
        }

        public CallResponseHandler(ServerboundCallResponseController callResponseController) {
            this.callResponseController = callResponseController;
        }

        @Override
        public PacketCallResponse handleServer(PacketCallResponse packet, ServerboundPacketContext context) {
            this.callResponseController.reply(
                    new CallResponse(packet.getCallerId(), packet.getCalleeId(), packet.getResponse()));
            return null;
        }
    }
}
