/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.eventhandler;

import io.github.singlerr.semaphore.gui.PhoneScreen;
import io.github.singlerr.semaphore.item.ItemPhone;
import io.github.singlerr.semaphore.regisries.CommonRegistries;
import io.github.singlerr.semaphore.state.State;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ItemInteractionHandler {

    private PhoneScreen phoneScreen;

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemPhone)) return;

        EntityPlayer player = event.getEntityPlayer();

        if (player == null) return;

        //        Optional<State<?>> opt = CommonRegistries.getStatePool().get(player.getUniqueID());
        //
        //        if (!opt.isPresent()) return;
        //
        //        if (!(opt.get() instanceof PlayerContext)) return;
        //
        //        PlayerContext context = (PlayerContext) opt.get();
        //
        //        if (!context.isUsingPhone()) return;

        if (phoneScreen == null) phoneScreen = new PhoneScreen(CommonRegistries.getStatePool());

        Minecraft.getMinecraft().displayGuiScreen(phoneScreen);
        phoneScreen.animate();
    }

    private void setPhoneUse(EntityPlayer player, boolean flag) {
        Optional<State<?>> opt = CommonRegistries.getStatePool().get(player.getUniqueID());

        if (!opt.isPresent()) return;

        if (!(opt.get() instanceof PlayerContext)) return;

        PlayerContext context = (PlayerContext) opt.get();

        context.setUsingPhone(flag);
    }

    private void openScreen(EntityPlayer player) {
        setPhoneUse(player, true);
        if (PhoneRenderer.getPhoneScreen() == null) PhoneRenderer.setPhoneScreen(new PhoneScreen(CommonRegistries.getStatePool()));
        PhoneRenderer.getPhoneScreen().setRefresh(true);
    }

    //    @SubscribeEvent
    public void onItemHeld(RenderGameOverlayEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        if (player == null) return;

        if (!player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()
                && player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemPhone) {
            //            openScreen(player);
            return;
        }

        if (!player.getHeldItem(EnumHand.OFF_HAND).isEmpty()
                && player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemPhone) {
            //            openScreen(player);
            return;
        }

        setPhoneUse(player, false);
    }
}
