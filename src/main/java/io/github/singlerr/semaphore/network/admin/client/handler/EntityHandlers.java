/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.client.handler;

import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.network.ClientboundPacketHandler;
import io.github.singlerr.semaphore.network.admin.client.ClientboundEntityPresenter;
import io.github.singlerr.semaphore.network.admin.packet.PacketPresentableEntities;
import io.github.singlerr.semaphore.network.admin.packet.PacketPresentableEntity;

public final class EntityHandlers {

    private EntityHandlers() {}

    public static final class PresentableEntitiesHandler
            extends ClientboundPacketHandler<PacketPresentableEntities, PacketPresentableEntities> {

        private ClientboundEntityPresenter entityController;

        public PresentableEntitiesHandler(ClientboundEntityPresenter entityController) {
            this.entityController = entityController;
        }

        public PresentableEntitiesHandler() {}

        @Override
        public PacketPresentableEntities handleClient(
                PacketPresentableEntities packet, ClientboundPacketContext context) {
            this.entityController.present(packet.getEntities());
            return null;
        }
    }

    public static final class PresentableEntityHandler
            extends ClientboundPacketHandler<PacketPresentableEntity, PacketPresentableEntity> {

        private ClientboundEntityPresenter entityController;

        public PresentableEntityHandler(ClientboundEntityPresenter entityController) {
            this.entityController = entityController;
        }

        public PresentableEntityHandler() {}

        @Override
        public PacketPresentableEntity handleClient(PacketPresentableEntity packet, ClientboundPacketContext context) {
            this.entityController.present(new PresentableEntity(
                    packet.getId(), new PresentableEntity.State(packet.getStateId(), packet.getMissCallCount())));
            return null;
        }
    }
}
