/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.eventhandler;

import io.github.singlerr.semaphore.item.ItemPhone;
import io.github.singlerr.semaphore.regisries.ClientRegistries;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ItemInteractionHandler {

    @SubscribeEvent
    public void onPhoneRightClick(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getItemStack().getItem() instanceof ItemPhone)) return;

        Minecraft.getMinecraft().displayGuiScreen(ClientRegistries.getPhoneScreen());
    }
}
