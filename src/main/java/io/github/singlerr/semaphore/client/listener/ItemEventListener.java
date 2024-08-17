/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.listener;

import io.github.singlerr.semaphore.client.gui.GuiControlPanel;
import io.github.singlerr.semaphore.instances.client.ClientResources;
import io.github.singlerr.semaphore.item.ItemControlPanel;
import io.github.singlerr.semaphore.item.ItemPhone;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ItemEventListener {

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Item item = event.getItemStack().getItem();

        if (item instanceof ItemControlPanel) {
            // Open control panel
            Minecraft.getMinecraft().displayGuiScreen(ClientResources.getInstance(GuiControlPanel.class));
            return;
        }

        if (item instanceof ItemPhone) {
            // Open phone
            return;
        }
    }
}
