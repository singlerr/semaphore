/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.block;

import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.block.entity.TileEntityPhoneBox;
import io.github.singlerr.semaphore.client.ServerWorldAwareInverseCallPresenter;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockPhoneBox extends Block implements ITileEntityProvider {

    private ServerWorldAwareInverseCallPresenter tracker;

    public BlockPhoneBox() {
        super(Material.AIR);
        setRegistryName(new ResourceLocation(Semaphore.MOD_ID, "block_phone_box"));
        setTranslationKey(Semaphore.MOD_ID + ".block_phone_box");
    }

    public void setTracker(ServerWorldAwareInverseCallPresenter tracker) {
        this.tracker = tracker;
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return new AxisAlignedBB(0, 0, 0, 1, 1, 1);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).build();
    }

    @Override
    public @Nullable TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityPhoneBox();
    }
}
