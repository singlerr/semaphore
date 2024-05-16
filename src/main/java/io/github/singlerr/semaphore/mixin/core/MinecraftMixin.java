/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    private MusicTicker musicTicker;

    @Shadow
    private SoundHandler soundHandler;

    @Redirect(
            method = "runTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/MusicTicker;update()V"))
    private void sempahore$removeMusicTickerCall(MusicTicker instance) {
        // Remove original calls
    }

    @Inject(
            method = "runTick",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;onPreClientTick()V",
                            shift = At.Shift.AFTER))
    private void semaphore$allowMusicTickerUpdateWhenever(CallbackInfo ci) {
        musicTicker.update();
        soundHandler.update();
    }

    @Redirect(
            method = "runTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundHandler;update()V"))
    private void semaphore$removeSoundHandlerCall(SoundHandler instance) {}
}
