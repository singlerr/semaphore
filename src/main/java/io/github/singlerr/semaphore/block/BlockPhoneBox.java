/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.block;

import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.block.entity.TileEntityPhoneBox;
import io.github.singlerr.semaphore.client.ClientWorldAwareInverseCallPresenter;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockPhoneBox extends Block implements ITileEntityProvider {

    private ClientWorldAwareInverseCallPresenter tracker;

    public BlockPhoneBox() {
        super(Material.ROCK);
        setRegistryName(new ResourceLocation(Semaphore.MOD_ID, "block_phone_box"));
        setTranslationKey(Semaphore.MOD_ID + ".block_phone_box");
    }

    public void setTracker(ClientWorldAwareInverseCallPresenter tracker) {
        this.tracker = tracker;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(new BlockPos(0, 0, 0));
    }

    @Override
    public @Nullable AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return null;
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
