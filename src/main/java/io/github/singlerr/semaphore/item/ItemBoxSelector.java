/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.item;

import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.block.BlockPhoneBox;
import io.github.singlerr.semaphore.block.entity.TileEntityPhoneBox;
import io.github.singlerr.semaphore.client.ClientWorldAwareInverseCallPresenter;
import io.github.singlerr.semaphore.instances.client.ClientResources;
import io.github.singlerr.semaphore.instances.common.CommonResources;
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityType;
import io.github.singlerr.semaphore.utils.Utils;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ItemBoxSelector extends Item {

    private final EntityController entityController;

    public ItemBoxSelector(EntityController entityController) {
        super();
        setRegistryName(new ResourceLocation(Semaphore.MOD_ID, "item_box_selector"));
        setTranslationKey(Semaphore.MOD_ID + ".box_selector");
        setMaxStackSize(1);

        this.entityController = entityController;
    }

    @Override
    public EnumActionResult onItemUse(
            EntityPlayer player,
            World worldIn,
            BlockPos pos,
            EnumHand hand,
            EnumFacing facing,
            float hitX,
            float hitY,
            float hitZ) {
        IBlockState clicked = worldIn.getBlockState(pos);
        UUID id = Utils.packToUUID(pos); // Pack BlockPos to UUID so that database can select
        if (clicked.getBlock() instanceof BlockPhoneBox) {
            // Remove Phone Box
            entityController.deleteEntity(new EntityQuery.DeleteEntity(id));
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
        } else {
            BlockPhoneBox block = CommonResources.getInstance(BlockPhoneBox.class);
            worldIn.setBlockState(pos, block.getDefaultState());
            entityController.createEntity(new EntityQuery.CreateEntityWithState(
                    id, new EntityQuery.State(0, new HashMap<>(), EntityType.PHONE_BOX)));

            // Client side TileEntity#getPos returns null, so we have to assign manually
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                TileEntityPhoneBox entity = (TileEntityPhoneBox) worldIn.getTileEntity(pos);
                entity.setPos(pos);

                ClientWorldAwareInverseCallPresenter presenter =
                        ClientResources.getInstance(ClientWorldAwareInverseCallPresenter.class);
                presenter.addTrackedTileEntity(entity);
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
