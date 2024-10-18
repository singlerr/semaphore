/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.caller.client.handlers;

import io.github.singlerr.semaphore.interactors.caller.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.Error;
import io.github.singlerr.semaphore.network.ClientboundPacketHandler;
import io.github.singlerr.semaphore.network.caller.packet.PacketError;

public final class ErrorHandlers {

    private ErrorHandlers() {
    }

    public static class ErrorHandler extends ClientboundPacketHandler<PacketError, PacketError> {

        public ErrorPresenter source;

        public ErrorHandler() {
        }

        public ErrorHandler(ErrorPresenter source) {
            this.source = source;
        }

        @Override
        public PacketError handleClient(PacketError packet, ClientboundPacketContext context) {
            this.source.present(new Error(packet.getCallerId(), packet.getCalleeId(), packet.getMessage()));
            return null;
        }
    }
}
