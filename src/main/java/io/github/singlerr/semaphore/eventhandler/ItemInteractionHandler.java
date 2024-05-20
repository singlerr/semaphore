/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.eventhandler;

import gg.essential.elementa.components.Window;
import io.github.singlerr.semaphore.gui.NotificationWindow;
import io.github.singlerr.semaphore.item.ItemPhone;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SideOnly(Side.CLIENT)
public final class ItemInteractionHandler {

    @SubscribeEvent
    public void onPhoneRightClick(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getItemStack().getItem() instanceof ItemPhone)) return;

        Minecraft.getMinecraft().displayGuiScreen(ClientRegistries.getPhoneScreen());
    }

    @SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {}
}
