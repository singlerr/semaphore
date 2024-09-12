/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client;

import io.github.singlerr.semaphore.block.entity.TileEntityPhoneBox;
import io.github.singlerr.semaphore.interactors.access.database.EntityType;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.utils.Utils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ClientWorldAwareInverseCallPresenter implements CallRequestPresenter, EntityPresenter {

    private final Map<UUID, TileEntityPhoneBox> trackedEntities;

    public ClientWorldAwareInverseCallPresenter() {
        this.trackedEntities = new ConcurrentHashMap<>();
    }

    public void addTrackedTileEntity(TileEntityPhoneBox phoneBox) {
        UUID id = Utils.packToUUID(phoneBox.getPos());
        this.trackedEntities.put(id, phoneBox);
    }

    public void removeTrackedTileEntity(TileEntityPhoneBox phoneBox) {
        UUID id = Utils.packToUUID(phoneBox.getPos());
        this.trackedEntities.remove(id);
    }

    @Override
    public void present(InverseCallRequest request) {
        TileEntityPhoneBox tileEntity = this.trackedEntities.get(request.getCalleeId());
        if (tileEntity == null) return;
        tileEntity.present(request);
    }

    @Override
    public void present(PresentableEntity entity) {
        if (entity.getState().getEntityType() != EntityType.PHONE_BOX) return;

        TileEntityPhoneBox tileEntity = this.trackedEntities.get(entity.getId());
        if (tileEntity == null) {
            BlockPos pos = Utils.fromUUID(entity.getId());
            TileEntity e = Minecraft.getMinecraft().player.world.getTileEntity(pos);
            if (e instanceof TileEntityPhoneBox) {
                tileEntity = (TileEntityPhoneBox) e;
                tileEntity.setPos(pos);
            }
        }

        if (tileEntity != null) tileEntity.present(entity);
    }

    @Override
    public void present(List<PresentableEntity> entities) {
        entities.forEach(this::present);
    }

    @Override
    public void presentError(ErrorEntity error) {}
}
