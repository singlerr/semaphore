/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.listener;

import io.github.singlerr.semaphore.block.entity.TileEntityPhoneBox;
import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController;
import io.github.singlerr.semaphore.policy.dfa.PlayerState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class BlockEventListener {

    private final CallResponseController responseController;

    public BlockEventListener(CallResponseController responseController) {
        this.responseController = responseController;
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        if (!(event.getWorld().getTileEntity(pos) instanceof TileEntityPhoneBox)) return;

        TileEntityPhoneBox phoneBox = (TileEntityPhoneBox) event.getWorld().getTileEntity(pos);
        if (phoneBox.getState() != null && phoneBox.getState() == PlayerState.RECEIVING_CALL) {}
    }
}
