package io.github.singlerr.semaphore.client.listener;

import io.github.singlerr.semaphore.client.gui.GuiControlPanel;
import io.github.singlerr.semaphore.instances.client.ClientResources;
import io.github.singlerr.semaphore.item.ItemControlPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ItemEventListener {

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event){
        if(event.getItemStack() == null)
            return;

        if(! (event.getItemStack().getItem() instanceof ItemControlPanel))
            return;

        GuiControlPanel controlPanel = ClientResources.getInstance(GuiControlPanel.class);
        Minecraft.getMinecraft().displayGuiScreen(controlPanel);
        controlPanel.onGuiOpened();
    }
}
