/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.item;

import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.block.BlockPhoneBox;
import io.github.singlerr.semaphore.instances.common.CommonResources;
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.utils.Utils;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        } else {
            BlockPhoneBox block = CommonResources.getInstance(BlockPhoneBox.class);
            worldIn.setBlockState(pos, block.getDefaultState());
            entityController.createEntity(new EntityQuery.CreateEntity(id));
        }
        return EnumActionResult.SUCCESS;
    }
}
