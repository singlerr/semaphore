/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.block;

import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.block.entity.TileEntityPhoneBox;
import io.github.singlerr.semaphore.client.ClientWorldAwareInverseCallPresenter;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.Nullable;

public class BlockPhoneBox extends Block implements ITileEntityProvider {

    private ClientWorldAwareInverseCallPresenter tracker;

    public BlockPhoneBox() {
        super(Material.ROCK);
        setRegistryName("block_phone_box");
        setTranslationKey(Semaphore.MOD_ID + ".block_phone_box");
    }

    public void setTracker(ClientWorldAwareInverseCallPresenter tracker) {
        this.tracker = tracker;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).build();
    }

    @Override
    public @Nullable TileEntity createNewTileEntity(World worldIn, int meta) {
        TileEntityPhoneBox tileEntity = new TileEntityPhoneBox();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT && tracker != null) {
            tileEntity.setTracker(tracker);
            tracker.addTrackedTileEntity(tileEntity);
        }
        return tileEntity;
    }
}
