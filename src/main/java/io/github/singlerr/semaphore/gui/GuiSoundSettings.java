/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui;

import io.github.singlerr.semaphore.config.ModConfig;
import io.github.singlerr.semaphore.gui.widgets.VolumeSlider;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiSoundSettings extends GuiScreen {

    private final GuiScreen parent;
    private ITextComponent title;

    public GuiSoundSettings(GuiScreen parentIn) {
        this.parent = parentIn;
    }

    @Override
    public void initGui() {
        super.initGui();
        title = new TextComponentTranslation("gui.settings.sound.title");

        int i = 2;
        int btnId = 0;

        // Miss call
        this.buttonList.add(VolumeSlider.builder()
                        .setParent(this)
                .setButtonId(btnId++)
                .setMessage("gui.settings.sound.player.miss.call")
                .setX(this.width / 2 - 155 + i % 2 * 160)
                .setY(this.height / 6 - 12 + 24 * (i >> 1))
                .setWidth(150)
                .setHeight(20)
                .setValue(ModConfig.soundSettings.missCallVolume)
                .setValueListener((v) -> {
                    ModConfig.soundSettings.missCallVolume = v;
                })
                .build());

        i++;

        this.buttonList.add(VolumeSlider.builder()
                .setParent(this)
                .setButtonId(btnId++)
                .setMessage("gui.settings.sound.player.in.call")
                .setX(this.width / 2 - 155 + i % 2 * 160)
                .setY(this.height / 6 - 12 + 24 * (i >> 1))
                .setWidth(150)
                .setHeight(20)
                .setValue(ModConfig.soundSettings.playerInCallVolume)
                .setValueListener((v) -> {
                    ModConfig.soundSettings.playerInCallVolume = v;
                })
                .build());

        i++;
        this.buttonList.add(VolumeSlider.builder()
                .setParent(this)
                .setButtonId(btnId++)
                .setMessage("gui.settings.sound.player.reject.call")
                .setX(this.width / 2 - 155 + i % 2 * 160)
                .setY(this.height / 6 - 12 + 24 * (i >> 1))
                .setWidth(150)
                .setHeight(20)
                .setValue(ModConfig.soundSettings.callDenyVolume)
                .setValueListener((v) -> {
                    ModConfig.soundSettings.callDenyVolume = v;
                })
                .build());

        i++;
        this.buttonList.add(VolumeSlider.builder()
                .setParent(this)
                .setButtonId(btnId++)
                .setMessage("gui.settings.sound.player.accept.call")
                .setX(this.width / 2 - 155 + i % 2 * 160)
                .setY(this.height / 6 - 12 + 24 * (i >> 1))
                .setWidth(150)
                .setHeight(20)
                .setValue(ModConfig.soundSettings.callAcceptVolume)
                .setValueListener((v) -> {
                    ModConfig.soundSettings.callAcceptVolume = v;
                })
                .build());

        i++;
        this.buttonList.add(VolumeSlider.builder()
                .setParent(this)
                .setButtonId(btnId++)
                .setMessage("gui.settings.sound.player.close.call")
                .setX(this.width / 2 - 155 + i % 2 * 160)
                .setY(this.height / 6 - 12 + 24 * (i >> 1))
                .setWidth(150)
                .setHeight(20)
                .setValue(ModConfig.soundSettings.callCloseVolume)
                .setValueListener((v) -> {
                    ModConfig.soundSettings.callCloseVolume = v;
                })
                .build());

        i++;
        this.buttonList.add(VolumeSlider.builder()
                .setParent(this)
                .setButtonId(btnId++)
                .setMessage("gui.settings.sound.player.calling")
                .setX(this.width / 2 - 155 + i % 2 * 160)
                .setY(this.height / 6 - 12 + 24 * (i >> 1))
                .setWidth(150)
                .setHeight(20)
                .setValue(ModConfig.soundSettings.callingVolume)
                .setValueListener((v) -> {
                    ModConfig.soundSettings.callingVolume = v;
                })
                .build());

        i++;

        this.buttonList.add(VolumeSlider.builder()
                .setParent(this)
                .setButtonId(btnId++)
                .setMessage("gui.settings.sound.player.touch")
                .setX(this.width / 2 - 155 + i % 2 * 160)
                .setY(this.height / 6 - 12 + 24 * (i >> 1))
                .setWidth(150)
                .setHeight(20)
                .setValue(ModConfig.soundSettings.touchVolume)
                .setValueListener((v) -> {
                    ModConfig.soundSettings.touchVolume = v;
                })
                .build());

        i++;

        this.buttonList.add(VolumeSlider.builder()
                .setParent(this)
                .setButtonId(btnId++)
                .setMessage("gui.settings.sound.player.ring")
                .setX(this.width / 2 - 155 + i % 2 * 160)
                .setY(this.height / 6 - 12 + 24 * (i >> 1))
                .setWidth(150)
                .setHeight(20)
                .setValue(ModConfig.soundSettings.ringVolume)
                .setValueListener((v) -> {
                    ModConfig.soundSettings.ringVolume = v;
                })
                .build());

        i++;

        this.buttonList.add(VolumeSlider.builder()
                .setParent(this)
                .setButtonId(btnId++)
                .setMessage("gui.settings.sound.player.vibrate")
                .setX(this.width / 2 - 155 + i % 2 * 160)
                .setY(this.height / 6 - 12 + 24 * (i >> 1))
                .setWidth(150)
                .setHeight(20)
                .setValue(ModConfig.soundSettings.vibrateVolume)
                .setValueListener((v) -> {
                    ModConfig.soundSettings.vibrateVolume = v;
                })
                .build());
        i++;

        int j = this.width / 2 - 75;
        int k = this.height / 6 - 12;
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title.getFormattedText(), this.width / 2, 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiButton component : buttonList) {
            if(! (component instanceof VolumeSlider))
                continue;

            if(component.isMouseOver()){
                ((VolumeSlider) component).onHover(mouseX, mouseY);
            }
        }
    }




    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 200){
            Minecraft.getMinecraft().displayGuiScreen(parent);
        }
    }
}
