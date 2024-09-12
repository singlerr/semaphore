/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.sound;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin {

    @Unique
    private SoundSystem soundSystem;

    @Inject(
            method = "updateAllSounds",
            at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void semaphore$adjustVolume(CallbackInfo ci, Iterator iterator, Map.Entry entry, String id) {
        ISound sound = (ISound) entry.getValue();

        if (soundSystem == null) {
            soundSystem = ObfuscationReflectionHelper.getPrivateValue(
                    SoundManager.class, ((SoundManager) (Object) this), "field_148620_e");
        }

        soundSystem.setVolume(id, sound.getVolume());
    }
}
