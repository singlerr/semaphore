package io.github.singlerr.semaphore.item;

import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.client.gui.GuiControlPanel;
import io.github.singlerr.semaphore.instances.client.ClientResources;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ItemControlPanel extends Item {

    public ItemControlPanel(){
        super();
        setRegistryName("control_panel");
        setTranslationKey(Semaphore.MOD_ID + ".control_panel");
        setMaxStackSize(1);
    }

}
