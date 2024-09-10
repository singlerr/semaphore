/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server.handler;

import io.github.singlerr.semaphore.interactors.admin.controller.data.CallStateQuery;
import io.github.singlerr.semaphore.network.ServerboundPacketHandler;
import io.github.singlerr.semaphore.network.admin.packet.*;
import io.github.singlerr.semaphore.network.admin.server.ServerboundCallStateController;

public final class CallStateHandlers {

    private CallStateHandlers() {}

    public static class GetCallStateHandler extends ServerboundPacketHandler<PacketGetCallState, PacketGetCallState> {

        private ServerboundCallStateController callStateController;

        public GetCallStateHandler() {}

        public GetCallStateHandler(ServerboundCallStateController callStateController) {
            this.callStateController = callStateController;
        }

        @Override
        public PacketGetCallState handleServer(PacketGetCallState packet, ServerboundPacketContext context) {
            CallStateQuery.GetCallState query = new CallStateQuery.GetCallState(packet.getId());
            query.setContext(context.getServerHandler().player);
            this.callStateController.getCallState(query);
            return null;
        }
    }

    public static class SetCallStateHandler extends ServerboundPacketHandler<PacketSetCallState, PacketSetCallState> {

        private ServerboundCallStateController callStateController;

        public SetCallStateHandler() {}

        public SetCallStateHandler(ServerboundCallStateController callStateController) {
            this.callStateController = callStateController;
        }

        @Override
        public PacketSetCallState handleServer(PacketSetCallState packet, ServerboundPacketContext context) {
            CallStateQuery.SetCallState query = new CallStateQuery.SetCallState(
                    packet.getId(), new CallStateQuery.State(packet.getStateId(), packet.getMissCallCount()));
            query.setContext(context.getServerHandler().player);
            this.callStateController.setCallState(query);
            return null;
        }
    }
}
