/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server.handler;

import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.network.ServerboundPacketHandler;
import io.github.singlerr.semaphore.network.admin.packet.*;
import io.github.singlerr.semaphore.network.admin.server.ServerboundEntityController;

public final class EntityHandlers {

    private EntityHandlers() {}

    public static class GetAllEntitiesHandler
            extends ServerboundPacketHandler<PacketGetAllEntities, PacketGetAllEntities> {

        private ServerboundEntityController entityController;

        public GetAllEntitiesHandler() {
            throw new IllegalStateException();
        }

        public GetAllEntitiesHandler(ServerboundEntityController entityController) {
            this.entityController = entityController;
        }

        @Override
        public PacketGetAllEntities handleServer(PacketGetAllEntities packet, ServerboundPacketContext context) {
            EntityQuery.GetAllEntities query = new EntityQuery.GetAllEntities();
            query.setContext(context.getServerHandler().player);
            this.entityController.getAllEntities(query);
            return null;
        }
    }

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
            EntityQuery.GetEntity query = new EntityQuery.GetEntity(packet.getId());
            query.setContext(context.getServerHandler().player);
            this.entityController.getEntity(query);
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
            this.entityController.createEntity(new EntityQuery.CreateEntity(packet.getId()));
            return null;
        }
    }

    public static class UpdateEntityHandler extends ServerboundPacketHandler<PacketUpdateEntity, PacketUpdateEntity> {

        private ServerboundEntityController entityController;

        public UpdateEntityHandler() {
            throw new IllegalStateException();
        }

        public UpdateEntityHandler(ServerboundEntityController entityController) {
            this.entityController = entityController;
        }

        @Override
        public PacketUpdateEntity handleServer(PacketUpdateEntity packet, ServerboundPacketContext context) {
            this.entityController.updateEntity(new EntityQuery.UpdateEntity(
                    packet.getId(),
                    new EntityQuery.State(packet.getStateId(), packet.getMissCallCount(), packet.getEntityType())));
            return null;
        }
    }

    public static class CreateEntityWithStateHandler
            extends ServerboundPacketHandler<PacketCreateEntityWithState, PacketCreateEntityWithState> {

        private ServerboundEntityController entityController;

        public CreateEntityWithStateHandler() {
            throw new IllegalStateException();
        }

        public CreateEntityWithStateHandler(ServerboundEntityController entityController) {
            this.entityController = entityController;
        }

        @Override
        public PacketCreateEntityWithState handleServer(
                PacketCreateEntityWithState packet, ServerboundPacketContext context) {
            this.entityController.createEntity(new EntityQuery.CreateEntityWithState(
                    packet.getId(),
                    new EntityQuery.State(packet.getStateId(), packet.getMissCallCount(), packet.getEntityType())));
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
