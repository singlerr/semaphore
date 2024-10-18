/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client;

import io.github.singlerr.semaphore.interactors.access.call.CallConnection;
import io.github.singlerr.semaphore.interactors.access.call.CallState;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.access.database.EntityType;
import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableCallConnection;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
public final class ClientSideEntityCache implements EntityPresenter, CallConnectionPresenter {

    @Nullable
    private ClientSideEntity entity;

    @Nullable
    private CallConnection callConnection;

    @Override
    public void present(PresentableEntity entity) {
        if (!Minecraft.getMinecraft().player.getUniqueID().equals(entity.getId())) return;
        this.entity = new ClientSideEntity(new Entity(
                entity.getId(),
                new Entity.State(
                        entity.getState().getStateId(),
                        entity.getState().getMissCallCount(),
                        entity.getState().getEntityType())));
    }

    @Override
    public void present(List<PresentableEntity> entities) {
        entities.stream()
                .filter(e -> e.getState().getEntityType() != EntityType.PHONE_BOX
                        && e.getId().equals(Minecraft.getMinecraft().player.getUniqueID()))
                .findAny()
                .ifPresent(entity -> {
                    this.entity = new ClientSideEntity(new Entity(
                            entity.getId(),
                            new Entity.State(
                                    entity.getState().getStateId(),
                                    entity.getState().getMissCallCount(),
                                    entity.getState().getEntityType())));
                });
    }

    @Override
    public void presentError(ErrorEntity error) {
    }

    @Override
    public void present(PresentableCallConnection entity) {
        UUID id = Minecraft.getMinecraft().player.getUniqueID();
        if (entity.getCalleeId().equals(id) || entity.getCallerId().equals(id))
            callConnection = new CallConnection(
                    entity.getId(),
                    entity.getCalleeId(),
                    entity.getCallerId(),
                    entity.isAlive() ? CallState.ALIVE : CallState.DEAD);
    }
}
