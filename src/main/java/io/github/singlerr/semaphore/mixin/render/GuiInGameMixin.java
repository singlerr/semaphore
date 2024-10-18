/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.render;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class GuiInGameMixin {

    @Inject(
            method = "renderHotbarItem",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getAnimationsToGo()I"))
    private void semaphore$applyShakingEffect_pre(
            int x, int y, float partialTicks, EntityPlayer player, ItemStack stack, CallbackInfo ci) {
        GlStateManager.rotate(400, 1, 0, 0);
    }

    @Inject(
            method = "renderHotbarItem",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getAnimationsToGo()I"))
    private void semaphore$applyShakingEffect_post(
            int x, int y, float partialTicks, EntityPlayer player, ItemStack stack, CallbackInfo ci) {
    }
}
