/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.block.entity;

import io.github.singlerr.semaphore.ModConfig;
import io.github.singlerr.semaphore.block.BlockPhoneBox;
import io.github.singlerr.semaphore.client.ServerWorldAwareInverseCallPresenter;
import io.github.singlerr.semaphore.client.sounds.SoundKey;
import io.github.singlerr.semaphore.client.sounds.SoundPlayerAccess;
import io.github.singlerr.semaphore.client.sounds.SoundResource;
import io.github.singlerr.semaphore.instances.DatabaseAccess;
import io.github.singlerr.semaphore.instances.common.CommonResources;
import io.github.singlerr.semaphore.instances.server.ServerResources;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.access.database.EntityType;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.policy.PolicyConstants;
import io.github.singlerr.semaphore.policy.dfa.PlayerState;
import io.github.singlerr.semaphore.utils.Utils;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TileEntityPhoneBox extends TileEntity implements ITickable, CallRequestPresenter, EntityPresenter {

    @Setter
    private ServerWorldAwareInverseCallPresenter tracker;

    @Getter
    private UUID id;

    @Getter
    private PlayerState state = PlayerState.DEFAULT;

    @Getter
    private UUID callerId;

    private SoundKey currentBelling;

    @Override
    public void update() {
        if (!world.isRemote) return;
        if (id == null || state == null) return;

        if (state == PlayerState.RECEIVING_CALL) {
            spawnSpiral(world, EnumParticleTypes.END_ROD, pos, 0.5f, 3.0f);
        }

        if (currentBelling != null) {
            double dist = Minecraft.getMinecraft().player.getPosition().getDistance(pos.getX(), pos.getY(), pos.getZ());
            float volume =
                    1.0f - MathHelper.clamp((float) dist, 0.0f, ModConfig.phoneBoxRadius) / ModConfig.phoneBoxRadius;
            currentBelling.getVolumeSetter().accept(volume);
        }
    }

    private void spawnSpiral(World world, EnumParticleTypes particle, BlockPos center, float radius, float height) {
        float delta = 0.5f;
        for (float x = 0; x < 2 * Math.PI; x += delta) {
            world.spawnParticle(
                    particle,
                    center.getX() + radius * Math.sin(x) + 0.5f,
                    center.getY() + height * (x / (2 * Math.PI)),
                    center.getZ() + radius * Math.cos(x) + 0.5f,
                    0,
                    0,
                    0);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = super.writeToNBT(compound);
        tag.setTag("phoneBoxData", serialize());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        deserialize(compound.getCompoundTag("phoneBoxData"));
    }

    @Override
    public @Nullable SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound compound = serialize();
        root.setTag("phoneBoxData", compound);
        return new SPacketUpdateTileEntity(getPos(), 1, root);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt.getNbtCompound().getCompoundTag("phoneBoxData");
        deserialize(tag);
        handleBell();
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        NBTTagCompound section = serialize();
        tag.setTag("phoneBoxData", section);
        return tag;
    }

    private NBTTagCompound serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setUniqueId("id", id);
        tag.setInteger("state", PolicyConstants.STATE_NFA.encode(state));
        if (callerId != null) tag.setUniqueId("caller", callerId);

        return tag;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return true;
    }

    private void deserialize(NBTTagCompound tag) {
        id = tag.getUniqueId("id");
        state = PolicyConstants.STATE_NFA.decode(tag.getInteger("state"));
        callerId = tag.getUniqueId("caller");
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        NBTTagCompound sectionTag = tag.getCompoundTag("phoneBoxData");
        deserialize(sectionTag);
        handleUpdate(new PresentableEntity.State(tag.getInteger("state"), new HashMap<>(), EntityType.PHONE_BOX));
    }

    private void handleBell() {
        if (currentBelling == null && state == PlayerState.RECEIVING_CALL) {
            currentBelling = SoundPlayerAccess.getInstance().playSound(SoundResource.BELL, 1.0f, 0.0f, true, false);
        }
    }

    private void handleUpdate(PresentableEntity.State state) {
        PlayerState playerState = PolicyConstants.STATE_NFA.decode(state.getStateId());
        this.state = playerState;

        if (!world.isRemote) {
            BlockPhoneBox block = CommonResources.getInstance(BlockPhoneBox.class);
            world.notifyBlockUpdate(pos, block.getDefaultState(), block.getDefaultState(), 1);
        }

        if (playerState != PlayerState.RECEIVING_CALL) {
            if (currentBelling != null) {
                SoundPlayerAccess.getInstance().stopSound(currentBelling);
                currentBelling = null;
            }
        } else {
            if (!world.isRemote) {
                return;
            }
            if (currentBelling == null) {
                currentBelling = SoundPlayerAccess.getInstance().playSound(SoundResource.BELL, 1.0f, 0.0f, true, false);
            }
        }
    }

    @Override
    public void present(InverseCallRequest request) {
        state = PlayerState.RECEIVING_CALL;
        callerId = request.getCallerId();
        // Receiving call
        if (!world.isRemote) {
            BlockPhoneBox block = CommonResources.getInstance(BlockPhoneBox.class);
            world.notifyBlockUpdate(pos, block.getDefaultState(), block.getDefaultState(), 1);
        }
    }

    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        this.id = Utils.packToUUID(posIn);
        if (tracker == null && !world.isRemote) {
            tracker = ServerResources.getInstance(ServerWorldAwareInverseCallPresenter.class);
        }

        if (!world.isRemote && tracker != null) {
            tracker.addTrackedTileEntity(this);
            DatabaseAccess.getInstance()
                    .create(
                            id,
                            new Entity(
                                    id,
                                    new Entity.State(
                                            PolicyConstants.STATE_NFA.encode(state),
                                            new HashMap<>(),
                                            EntityType.PHONE_BOX)));
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        // Remove request presenter from adapter
        if (tracker != null) {
            tracker.removeTrackedTileEntity(this);
        }

        if (currentBelling != null) SoundPlayerAccess.getInstance().stopSound(currentBelling);
    }

    @Override
    public void present(PresentableEntity entity) {
        if (id == null) return;
        if (!entity.getId().equals(id)) return;
        handleUpdate(entity.getState());
    }

    @Override
    public void present(List<PresentableEntity> entities) {}

    @Override
    public void presentError(ErrorEntity error) {}
}
