/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiEntityList extends GuiListExtended {

    private final List<GuiEntityEntry> entries;

    public GuiEntityList(int width, int height, int top, int bottom, int size) {
        super(Minecraft.getMinecraft(), width, height, top, bottom, size);
        this.entries = Collections.synchronizedList(new ArrayList<>());
    }

    public GuiEntityList(int width, int height, int top, int bottom, int size, List<GuiEntityEntry> entryList) {
        super(Minecraft.getMinecraft(), width, height, top, bottom, size);
        this.entries = entryList;
    }

    public static void enableScissor(int x1, int y1, int x2, int y2) {
        GL11.glEnable(3089);
        GL11.glScissor(x1, y1, x2, y2);
    }

    public static void disableScissor() {
        GL11.glDisable(3089);
    }

    @Override
    public int getListWidth() {
        return this.width - 15;
    }

    public void addEntry(GuiEntityEntry entry) {
        this.entries.add(entry);
    }

    public List<GuiEntityEntry> getEntries() {
        return entries;
    }

    @Override
    public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks) {
        synchronized (entries) {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            double scale = scaledResolution.getScaleFactor();
            int scaledHeight = scaledResolution.getScaledHeight();
            enableScissor(0, (int) ((double) (scaledHeight - this.bottom) * scale), 1073741823, (int)
                    ((double) this.height * scale));
            super.drawScreen(mouseXIn, mouseYIn, partialTicks);
            disableScissor();
        }
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        return entries.get(index);
    }

    @Override
    protected int getSize() {
        return entries.size();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
    }

    @Override
    protected void drawBackground() {
        // No-op
    }

    @Override
    protected void drawContainerBackground(Tessellator tessellator) {
        // No-op
    }

    @Override
    protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {
        // No-op
    }

    @Override
    protected int getContentHeight() {
        return Math.max(super.getContentHeight(), 1);
    }
}
