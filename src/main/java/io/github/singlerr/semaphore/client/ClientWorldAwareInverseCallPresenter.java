/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client;

import io.github.singlerr.semaphore.block.entity.TileEntityPhoneBox;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.utils.Utils;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientWorldAwareInverseCallPresenter implements CallRequestPresenter {

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
        TileEntityPhoneBox tileEntity = this.trackedEntities.get(request.calleeId());
        if (tileEntity == null) return;
        tileEntity.present(request);
    }
}
