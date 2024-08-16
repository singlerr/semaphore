/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server.handler;

import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.network.ServerboundPacketHandler;
import io.github.singlerr.semaphore.network.admin.packet.PacketCreateEntity;
import io.github.singlerr.semaphore.network.admin.packet.PacketDeleteEntity;
import io.github.singlerr.semaphore.network.admin.packet.PacketGetEntity;
import io.github.singlerr.semaphore.network.admin.server.ServerboundEntityController;

public final class EntityHandlers {

    private EntityHandlers() {}

    public static class GetEntityHandler extends ServerboundPacketHandler<PacketGetEntity, PacketGetEntity> {

        private ServerboundEntityController entityController;

        public GetEntityHandler() {
            throw new IllegalStateException();
        }

        public GetEntityHandler(ServerboundEntityController entityController) {
            this.entityController = entityController;
        }

        @Override
        public PacketGetEntity handleServer(PacketGetEntity packet, ServerboundPacketContext context) {
            this.entityController.getEntity(new EntityQuery.GetEntity(packet.getId()));
            return null;
        }
    }

    public static class CreateEntityHandler extends ServerboundPacketHandler<PacketCreateEntity, PacketCreateEntity> {

        private ServerboundEntityController entityController;

        public CreateEntityHandler() {
            throw new IllegalStateException();
        }

        public CreateEntityHandler(ServerboundEntityController entityController) {
            this.entityController = entityController;
        }

        @Override
        public PacketCreateEntity handleServer(PacketCreateEntity packet, ServerboundPacketContext context) {
            this.entityController.createEntity(new EntityQuery.CreateEntity());
            return null;
        }
    }

    public static class DeleteEntityHandler extends ServerboundPacketHandler<PacketDeleteEntity, PacketDeleteEntity> {

        private ServerboundEntityController entityController;

        public DeleteEntityHandler() {
            throw new IllegalStateException();
        }

        public DeleteEntityHandler(ServerboundEntityController entityController) {
            this.entityController = entityController;
        }

        @Override
        public PacketDeleteEntity handleServer(PacketDeleteEntity packet, ServerboundPacketContext context) {
            this.entityController.deleteEntity(new EntityQuery.DeleteEntity(packet.getId()));
            return null;
        }
    }
}
