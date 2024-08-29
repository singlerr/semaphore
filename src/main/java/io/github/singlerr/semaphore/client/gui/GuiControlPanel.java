/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.gui;

import io.github.singlerr.semaphore.interactors.admin.controller.CallConnectionController;
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController;
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableCallConnection;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController;
import io.github.singlerr.semaphore.policy.admin.presenters.CallConnectionPresenterAdapter;
import io.github.singlerr.semaphore.policy.admin.presenters.EntityPresenterAdapter;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

@Log4j2
public final class GuiControlPanel extends GuiScreen implements CallConnectionPresenter, EntityPresenter {

    private static final ResourceLocation TEXTURE_WINDOW = new ResourceLocation("textures/gui/advancements/window.png");

    private final EntityController entityController;
    private final CallConnectionController callConnectionController;
    private final CallStateController callStateController;

    private final int textureWidth = 252;
    private final int textureHeight = 140;

    private int width;
    private int height;

    private final int xPadding = 130;
    private final int yPadding = 50;

    private final int btnXPadding = 10;

    private int xOffset;
    private int yOffset;

    private GuiButton btnOpenPhoneBoxControlPanel;
    private GuiButton btnOpenUserControlPanel;

    private final GuiPhoneBoxControlPanel phoneBoxControlPanel;
    private final GuiUserControlPanel userControlPanel;

    public GuiControlPanel(
            EntityController entityController,
            CallConnectionController callConnectionController,
            CallStateController callStateController,
            CallRequestController requestController) {
        this.entityController = entityController;
        this.callConnectionController = callConnectionController;
        this.callStateController = callStateController;
        this.mc = Minecraft.getMinecraft();

        this.phoneBoxControlPanel = new GuiPhoneBoxControlPanel(
                this, entityController, callConnectionController, callStateController, requestController);
        this.userControlPanel = new GuiUserControlPanel(
                this, entityController, callConnectionController, callStateController, requestController);
    }

    @Override
    public void initGui() {
        super.initGui();

        ScaledResolution resolution = new ScaledResolution(mc);

        this.width = resolution.getScaledWidth() - xPadding * 2;
        this.height = resolution.getScaledHeight() - yPadding * 2;

        this.xOffset = (resolution.getScaledWidth() - this.width) / 2;
        this.yOffset = (resolution.getScaledHeight() - this.height) / 2;

        int btnWidth = width - 18 - btnXPadding * 2;
        int btnHeight = 20;
        this.btnOpenPhoneBoxControlPanel =
                new GuiButton(0, xOffset + 20, yOffset + 30, btnWidth, btnHeight, "Phone Box Control Panel");
        this.btnOpenUserControlPanel =
                new GuiButton(1, xOffset + 20, yOffset + 70, btnWidth, btnHeight, "User Control Panel");

        addButton(this.btnOpenPhoneBoxControlPanel);
        addButton(this.btnOpenUserControlPanel);

        this.phoneBoxControlPanel.initGui();
        this.userControlPanel.initGui();
    }

    @Override
    public void present(List<PresentableEntity> entities) {
        this.userControlPanel.present(entities);
        this.phoneBoxControlPanel.present(entities);
    }

    @Override
    public void present(PresentableCallConnection entity) {
        this.userControlPanel.present(entity);
        this.phoneBoxControlPanel.present(entity);
    }

    @Override
    public void present(PresentableEntity entity) {
        this.userControlPanel.present(entity);
        this.phoneBoxControlPanel.present(entity);
    }

    @Override
    public void presentError(ErrorEntity error) {
        this.userControlPanel.presentError(error);
        this.phoneBoxControlPanel.presentError(error);
    }

    public boolean shouldPresent(CallConnectionPresenterAdapter.PresenterContext context) {
        return this.userControlPanel.shouldPresent(context) || this.phoneBoxControlPanel.shouldPresent(context);
    }

    public boolean shouldPresent(EntityPresenterAdapter.PresenterContext context) {
        return this.userControlPanel.shouldPresent(context) || this.phoneBoxControlPanel.shouldPresent(context);
    }

    @Override
    public void drawBackground(int tint) {
        super.drawBackground(tint);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawWindow(xOffset, yOffset);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawWindow(int x, int y) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(TEXTURE_WINDOW);
        drawScaledCustomSizeModalRect(x, y, 0, 0, textureWidth, textureHeight, width, height, 256, 256);
        drawRect(x + 7, y + 18, x + width - 4, y + height - 10, Color.GRAY.getRGB());
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == this.btnOpenPhoneBoxControlPanel.id) {
            Minecraft.getMinecraft().displayGuiScreen(this.phoneBoxControlPanel);
            return;
        }

        if (button.id == this.btnOpenUserControlPanel.id) {
            Minecraft.getMinecraft().displayGuiScreen(this.userControlPanel);
            return;
        }
    }

}
