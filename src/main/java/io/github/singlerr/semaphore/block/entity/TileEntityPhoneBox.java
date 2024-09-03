/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.block.entity;

import io.github.singlerr.semaphore.client.ClientWorldAwareInverseCallPresenter;
import io.github.singlerr.semaphore.instances.client.ClientResources;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityPhoneBox extends TileEntity implements ITickable, CallRequestPresenter {

    private ClientWorldAwareInverseCallPresenter tracker;

    public void setTracker(ClientWorldAwareInverseCallPresenter tracker) {
        this.tracker = tracker;
    }

    @Override
    public void update() {}

    @Override
    public void present(InverseCallRequest request) {
        // Receiving call
        if (world != null) {
            world.playRecord(pos, ClientResources.StaticResources.SOUND_RECEIVING_CALL);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        // Remove request presenter from adapter
        if (tracker != null) {
            tracker.removeTrackedTileEntity(this);
        }
    }
}
