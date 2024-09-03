/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.listener;

import io.github.singlerr.access.semaphore.client.gui.NonVanillaScreen;
import io.github.singlerr.access.semaphore.client.gui.NonVanillaScreenAccess;
import io.github.singlerr.semaphore.client.gui.GuiControlPanel;
import io.github.singlerr.semaphore.instances.client.ClientResources;
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.item.ItemControlPanel;
import io.github.singlerr.semaphore.item.ItemPhone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ItemEventListener {

    private final EntityController entityController;

    public ItemEventListener(EntityController entityController) {
        this.entityController = entityController;
    }

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
            NonVanillaScreen scr = ClientResources.getInstance(NonVanillaScreen.class);
            if (scr == null && NonVanillaScreenAccess.getFactory() != null) {
                scr = NonVanillaScreenAccess.getFactory()
                        .create(ClientResources.getInstance(NonVanillaScreen.FactoryParams.class));
                if (scr != null) ClientResources.setInstance(NonVanillaScreen.class, scr);
            }

            if (scr instanceof GuiScreen) {
                Minecraft.getMinecraft().displayGuiScreen((GuiScreen) scr);
            }
        }
    }
}
