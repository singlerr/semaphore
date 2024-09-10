/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server.handler;

import io.github.singlerr.semaphore.interactors.admin.controller.data.CallStateQuery;
import io.github.singlerr.semaphore.network.ServerboundPacketHandler;
import io.github.singlerr.semaphore.network.admin.packet.*;
import io.github.singlerr.semaphore.network.admin.server.ServerboundCallStateController;

public final class CallStateHandlers {

    private CallStateHandlers() {}

    public static class CloseCallHandler extends ServerboundPacketHandler<PacketCloseCall, PacketCloseCall> {

        private ServerboundCallStateController callStateController;

        public CloseCallHandler() {}

        public CloseCallHandler(ServerboundCallStateController callStateController) {
            this.callStateController = callStateController;
        }

        @Override
        public PacketCloseCall handleServer(PacketCloseCall packet, ServerboundPacketContext context) {
            CallStateQuery.CloseCall query = new CallStateQuery.CloseCall(packet.callerId(), packet.calleeId());
            query.setContext(context.getServerHandler().player);
            this.callStateController.closeCall(query);
            return null;
        }
    }

    public static class CloseCallByIdHandler
            extends ServerboundPacketHandler<PacketCloseCallById, PacketCloseCallById> {

        private ServerboundCallStateController callStateController;

        public CloseCallByIdHandler() {}

        public CloseCallByIdHandler(ServerboundCallStateController callStateController) {
            this.callStateController = callStateController;
        }

        @Override
        public PacketCloseCallById handleServer(PacketCloseCallById packet, ServerboundPacketContext context) {
            CallStateQuery.CloseCallById query = new CallStateQuery.CloseCallById(packet.getId());
            query.setContext(context.getServerHandler().player);
            this.callStateController.closeCall(query);
            return null;
        }
    }

    public static class OpenCallHandler extends ServerboundPacketHandler<PacketOpenCall, PacketOpenCall> {

        private ServerboundCallStateController callStateController;

        public OpenCallHandler() {}

        public OpenCallHandler(ServerboundCallStateController callStateController) {
            this.callStateController = callStateController;
        }

        @Override
        public PacketOpenCall handleServer(PacketOpenCall packet, ServerboundPacketContext context) {
            CallStateQuery.OpenCall query = new CallStateQuery.OpenCall(packet.getCallerId(), packet.getCalleeId());
            query.setContext(context.getServerHandler().player);
            this.callStateController.openCall(query);
            return null;
        }
    }
}
