/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server.handler;

import io.github.singlerr.semaphore.interactors.admin.controller.data.CallConnectionQuery;
import io.github.singlerr.semaphore.network.ServerboundPacketHandler;
import io.github.singlerr.semaphore.network.admin.packet.PacketCloseConnection;
import io.github.singlerr.semaphore.network.admin.packet.PacketGetConnection;
import io.github.singlerr.semaphore.network.admin.packet.PacketOpenConnection;
import io.github.singlerr.semaphore.network.admin.server.ServerboundCallConnectionController;

public final class CallConnectionHandlers {

    private CallConnectionHandlers() {}

    public static class CloseConnectionHandler
            extends ServerboundPacketHandler<PacketCloseConnection, PacketCloseConnection> {

        private ServerboundCallConnectionController connectionController;

        public CloseConnectionHandler() {
            throw new IllegalStateException();
        }

        public CloseConnectionHandler(ServerboundCallConnectionController callConnectionController) {
            this.connectionController = callConnectionController;
        }

        @Override
        public PacketCloseConnection handleServer(PacketCloseConnection packet, ServerboundPacketContext context) {
            CallConnectionQuery.CloseConnection con = new CallConnectionQuery.CloseConnection(packet.getId());
            con.setContext(context.getServerHandler().player);
            this.connectionController.closeConnection(con);
            return null;
        }
    }

    public static class OpenConnectionHandler
            extends ServerboundPacketHandler<PacketOpenConnection, PacketOpenConnection> {

        private ServerboundCallConnectionController connectionController;

        public OpenConnectionHandler() {
            throw new IllegalStateException();
        }

        public OpenConnectionHandler(ServerboundCallConnectionController connectionController) {
            this.connectionController = connectionController;
        }

        @Override
        public PacketOpenConnection handleServer(PacketOpenConnection packet, ServerboundPacketContext context) {
            CallConnectionQuery.OpenConnection con =
                    new CallConnectionQuery.OpenConnection(packet.getCallerId(), packet.getCalleeId());
            con.setContext(context.getServerHandler().player);
            this.connectionController.openConnection(con);
            return null;
        }
    }

    public static class GetConnectionHandler
            extends ServerboundPacketHandler<PacketGetConnection, PacketGetConnection> {

        private ServerboundCallConnectionController connectionController;

        public GetConnectionHandler() {
            throw new IllegalStateException();
        }

        public GetConnectionHandler(ServerboundCallConnectionController connectionController) {
            this.connectionController = connectionController;
        }

        @Override
        public PacketGetConnection handleServer(PacketGetConnection packet, ServerboundPacketContext context) {
            CallConnectionQuery.GetConnection con = new CallConnectionQuery.GetConnection(packet.getId());
            con.setContext(context.getServerHandler().player);
            this.connectionController.getConnection(con);
            return null;
        }
    }
}
