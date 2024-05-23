/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.core.gui;

import io.github.singlerr.semaphore.gui.GuiSoundSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiScreenOptionsSounds.class)
public abstract class GuiScreenOptionsSoundsMixin extends GuiScreen {

    @Shadow @Final private GuiScreen parent;

    @ModifyArg(method = "initGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiOptionButton;<init>(IIILnet/minecraft/client/settings/GameSettings$Options;Ljava/lang/String;)V"), index = 1)
    private int semaphore$moveSubtitleOptionButton(int i){
        return this.width / 2 - 155 + (i % 2 == 0 ? i - 1 : i) % 2 * 160;
    }


    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiOptionButton;<init>(IIILnet/minecraft/client/settings/GameSettings$Options;Ljava/lang/String;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void semaphore$addSoundSettingsButton(CallbackInfo ci, int i){
        buttonList.add(new GuiButton(203,width / 2 - 155, (this.height / 6 - 12) + 24 * (i >> 1), 150, 20, I18n.format("gui.settings.sound.open")));
    }

    @Inject(method = "actionPerformed", at = @At(value = "HEAD"), cancellable = true)
    private void semaphore$onSoundSettingsButtonClicked(GuiButton button, CallbackInfo ci){
        if(button.id != 203)
            return;

        Minecraft.getMinecraft().displayGuiScreen(new GuiSoundSettings(parent));
        ci.cancel();
    }
}
