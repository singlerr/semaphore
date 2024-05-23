/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.widgets;

import de.maxhenkel.voicechat.gui.widgets.DebouncedSlider;
import java.awt.*;
import java.util.function.Consumer;
import lombok.Builder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class VolumeSlider extends DebouncedSlider {

    private final Consumer<Double> valueListener;

    private final String message;

    private final GuiScreen parent;

    public VolumeSlider(
            GuiScreen parent,
            int buttonId,
            int x,
            int y,
            int width,
            int height,
            double value,
            String message,
            Consumer<Double> valueListener) {
        super(buttonId, x, y, width, height, value);
        this.parent = parent;
        this.message = message;
        this.valueListener = valueListener;
        displayString = I18n.format(message, String.format("%d%%", ((int) (value * 100))));
    }

    @Override
    public void applyDebounced() {
        valueListener.accept(value);
    }

    @Override
    protected void updateMessage() {
        displayString = I18n.format(message, String.format("%d%%", ((int) (value * 100))));
    }

    public static Builder builder() {
        return new Builder();
    }

    public void onHover( int mouseX, int mouseY) {
        parent.drawHoveringText(I18n.format(message + ".hover"), mouseX, mouseY);
    }

    public static class Builder {
        private GuiScreen parent;
        private int buttonId;
        private int x;
        private int y;
        private int width;
        private int height;
        private double value;

        private Consumer<Double> valueListener;
        private String message;

        Builder() {}

        public Builder setParent(GuiScreen parent){
            this.parent = parent;
            return this;
        }

        public Builder setButtonId(int buttonId) {
            this.buttonId = buttonId;
            return this;
        }

        public Builder setX(int x) {
            this.x = x;
            return this;
        }

        public Builder setY(int y) {
            this.y = y;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setValue(double value) {
            this.value = value;
            return this;
        }

        public Builder setValueListener(Consumer<Double> valueListener) {
            this.valueListener = valueListener;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public VolumeSlider build() {
            return new VolumeSlider(parent, buttonId, x, y, width, height, value, message, valueListener);
        }
    }
}
