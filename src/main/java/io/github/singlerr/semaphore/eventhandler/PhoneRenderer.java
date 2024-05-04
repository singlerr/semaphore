/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.eventhandler;

import io.github.singlerr.semaphore.regisries.CommonRegistries;
import io.github.singlerr.semaphore.state.State;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PhoneRenderer {

    @SubscribeEvent
    public void renderPhone(RenderGameOverlayEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        Optional<State<?>> opt = CommonRegistries.getStatePool().get(player.getUniqueID());

        if (!opt.isPresent()) {
            return;
        }

        if (!(opt.get() instanceof PlayerContext)) return;

        PlayerContext state = (PlayerContext) opt.get();

        if (!state.isUsingPhone()) return;

        //        PhoneScreen screen = new PhoneScreen(CommonRegistries.getStatePool());
        //        screen.drawBackground(event.getPartialTicks());
    }
}
