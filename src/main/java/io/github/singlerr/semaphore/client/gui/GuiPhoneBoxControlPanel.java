/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.gui;

import io.github.singlerr.semaphore.client.gui.widget.GuiEntityEntry;
import io.github.singlerr.semaphore.client.gui.widget.GuiEntityList;
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
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public final class GuiPhoneBoxControlPanel extends GuiScreen implements EntityPresenter, CallConnectionPresenter {
    private static final ResourceLocation TEXTURE_WINDOW = new ResourceLocation("textures/gui/advancements/window.png");

    private final GuiControlPanel parent;

    private final EntityController entityController;
    private final CallConnectionController callConnectionController;
    private final CallStateController callStateController;
    private final CallRequestController requestController;

    private final int xPadding = 20;
    private final int yPadding = 10;

    private final int textureWidth = 252;
    private final int textureHeight = 140;

    private int xOffset;
    private int yOffset;

    private GuiEntityList entityList;

    public GuiPhoneBoxControlPanel(
            GuiControlPanel parent,
            EntityController entityController,
            CallConnectionController callConnectionController,
            CallStateController stateController,
            CallRequestController requestController) {
        super();
        this.parent = parent;
        this.entityController = entityController;
        this.callConnectionController = callConnectionController;
        this.callStateController = stateController;
        this.requestController = requestController;
        this.mc = Minecraft.getMinecraft();
    }

    @Override
    public void initGui() {
        super.initGui();

        ScaledResolution resolution = new ScaledResolution(mc);

        this.width = resolution.getScaledWidth() - xPadding * 2;
        this.height = resolution.getScaledHeight() - yPadding * 2;

        this.xOffset = (resolution.getScaledWidth() - this.width) / 2;
        this.yOffset = (resolution.getScaledHeight() - this.height) / 2;

        int listHeight = this.height - 40;
        this.entityList = new GuiEntityList(width, listHeight, yOffset + 30, yOffset + 30 + listHeight, 60);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWindow(mouseX, mouseY, partialTicks);
        this.entityList.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawWindow(int mouseX, int mouseY, float partialTicks) {
        drawRect(xOffset + 9, yOffset + 18, xOffset + width - 9, yOffset + height - 10, Color.LIGHT_GRAY.getRGB());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(TEXTURE_WINDOW);
        drawScaledCustomSizeModalRect(xOffset, yOffset, 0, 0, textureWidth, textureHeight, width, height, 256, 256);
        fontRenderer.drawStringWithShadow("Phone Box Control Panel", xOffset + 10, yOffset + 10, Color.WHITE.getRGB());
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.entityList.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.entityList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.entityList.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        this.entityList.actionPerformed(button);
    }

    @Override
    public void present(PresentableEntity entity) {
        NetworkPlayerInfo info = mc.getConnection().getPlayerInfo(entity.id());
        if (info != null) return;
        Optional<GuiEntityEntry> entry = this.entityList.getEntries().stream()
                .filter(e -> e.getEntity().id().equals(entity.id()))
                .findAny();
        if (entry.isPresent()) {
            entry.get().setEntity(entity);
        } else {
            this.entityList.addEntry(new GuiEntityEntry(
                    entityController, callConnectionController, callStateController, requestController, entity));
        }
    }

    @Override
    public void present(List<PresentableEntity> entities) {
        this.entityList.getEntries().clear();
        this.entityList
                .getEntries()
                .addAll(entities.stream()
                        .filter(e -> mc.getConnection().getPlayerInfo(e.id()) == null)
                        .map(e -> new GuiEntityEntry(
                                entityController, callConnectionController, callStateController, requestController, e))
                        .collect(Collectors.toList()));
    }

    @Override
    public void presentError(ErrorEntity error) {}

    @Override
    public void present(PresentableCallConnection entity) {
        this.entityList.getEntries().stream()
                .filter(e -> e.getEntity().id().equals(entity.calleeId())
                        || e.getEntity().id().equals(entity.callerId()))
                .findAny()
                .ifPresent(e -> e.setCurrentConnection(entity));
    }

    public boolean shouldPresent(CallConnectionPresenterAdapter.PresenterContext context) {
        return true;
    }

    public boolean shouldPresent(EntityPresenterAdapter.PresenterContext context) {
        return true;
    }
}
