/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.gui;

import net.minecraft.client.gui.GuiScreen;

public abstract class GuiRenderHandler extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GuiRenderContext context = new GuiRenderContext(mouseX, mouseY, partialTicks);
        draw(context);
    }

    public abstract void draw(GuiRenderContext context);
}
