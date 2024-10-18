/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.client.handler;

import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableCallConnection;
import io.github.singlerr.semaphore.network.ClientboundPacketHandler;
import io.github.singlerr.semaphore.network.admin.client.ClientboundCallConnectionPresenter;
import io.github.singlerr.semaphore.network.admin.packet.PacketErrorEntity;
import io.github.singlerr.semaphore.network.admin.packet.PacketPresentableCallConnection;

public final class CallConnectionHandlers {

    private CallConnectionHandlers() {
    }

    public static class PresentableCallConnectionHandler
            extends ClientboundPacketHandler<PacketPresentableCallConnection, PacketPresentableCallConnection> {

        private ClientboundCallConnectionPresenter connectionPresenter;

        public PresentableCallConnectionHandler() {
        }

        public PresentableCallConnectionHandler(ClientboundCallConnectionPresenter presenter) {
            this.connectionPresenter = presenter;
        }

        @Override
        public PacketPresentableCallConnection handleClient(
                PacketPresentableCallConnection packet, ClientboundPacketContext context) {
            this.connectionPresenter.present(new PresentableCallConnection(
                    packet.getId(), packet.getCallerId(), packet.getCalleeId(), packet.isAlive()));
            return null;
        }
    }

    public static class ErrorEntityHandler extends ClientboundPacketHandler<PacketErrorEntity, PacketErrorEntity> {

        private ClientboundCallConnectionPresenter connectionPresenter;

        public ErrorEntityHandler() {
        }

        public ErrorEntityHandler(ClientboundCallConnectionPresenter presenter) {
            this.connectionPresenter = presenter;
        }

        @Override
        public PacketErrorEntity handleClient(PacketErrorEntity packet, ClientboundPacketContext context) {
            this.connectionPresenter.presentError(new ErrorEntity(packet.getMessage()));
            return null;
        }
    }
}
