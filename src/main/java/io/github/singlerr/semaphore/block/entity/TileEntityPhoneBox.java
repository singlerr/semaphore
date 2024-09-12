/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.block.entity;

import io.github.singlerr.semaphore.ModConfig;
import io.github.singlerr.semaphore.client.ClientWorldAwareInverseCallPresenter;
import io.github.singlerr.semaphore.client.sounds.SoundKey;
import io.github.singlerr.semaphore.client.sounds.SoundPlayerAccess;
import io.github.singlerr.semaphore.client.sounds.SoundResource;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.policy.PolicyConstants;
import io.github.singlerr.semaphore.policy.dfa.PlayerState;
import io.github.singlerr.semaphore.utils.Utils;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TileEntityPhoneBox extends TileEntity implements ITickable, CallRequestPresenter, EntityPresenter {

    @Setter
    private ClientWorldAwareInverseCallPresenter tracker;

    private UUID id;

    @Getter
    private PlayerState state;

    private SoundKey currentBelling;

    @Override
    public void update() {
        if (!world.isRemote) return;
        if (id == null || state == null) return;

        if (state == PlayerState.RECEIVING_CALL) {
            spawnSpiral(world, EnumParticleTypes.FIREWORKS_SPARK, pos, 0.5f, 2.0f);
        }

        if (currentBelling != null) {
            double dist = Minecraft.getMinecraft().player.getPosition().getDistance(pos.getX(), pos.getY(), pos.getZ());
            float volume =
                    1.0f - MathHelper.clamp((float) dist, 0.0f, ModConfig.phoneBoxRadius) / ModConfig.phoneBoxRadius;
            currentBelling.getVolumeSetter().accept(volume);
        }
    }
    // -244 218
    private void spawnSpiral(World world, EnumParticleTypes particle, BlockPos center, float radius, float height) {
        float delta = 0.5f;

        for (float x = 0; x < 2 * Math.PI; x += delta) {
            world.spawnParticle(
                    particle,
                    center.getX() + radius * Math.cos(x),
                    center.getY() + height * (x / (2 * Math.PI)),
                    center.getZ() + radius * Math.sin(x),
                    0,
                    0,
                    0);
        }
    }

    private void handleUpdate(PresentableEntity.State state) {
        PlayerState playerState = PolicyConstants.STATE_NFA.decode(state.getStateId());
        this.state = playerState;
        if (playerState != PlayerState.RECEIVING_CALL) {
            if (currentBelling != null) {
                SoundPlayerAccess.getInstance().stopSound(currentBelling);
                currentBelling = null;
            }
        } else {
            if (world != null && currentBelling == null) {
                currentBelling = SoundPlayerAccess.getInstance().playSound(SoundResource.BELL, 1.0f, 0.0f, true, false);
            }
        }
    }

    @Override
    public void present(InverseCallRequest request) {
        if (!world.isRemote) return;

        state = PlayerState.RECEIVING_CALL;
        // Receiving call
        if (world != null) {
            currentBelling = SoundPlayerAccess.getInstance().playSound(SoundResource.BELL, 1.0f, 0.0f, true, false);
        }
    }

    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        this.id = Utils.packToUUID(posIn);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        // Remove request presenter from adapter
        if (tracker != null) {
            tracker.removeTrackedTileEntity(this);
        }
    }

    @Override
    public void present(PresentableEntity entity) {
        if (!world.isRemote) return;
        if (id == null) return;
        if (!entity.getId().equals(id)) return;
        handleUpdate(entity.getState());
    }

    @Override
    public void present(List<PresentableEntity> entities) {}

    @Override
    public void presentError(ErrorEntity error) {}
}
