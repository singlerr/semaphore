/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.gui.widget;

import de.maxhenkel.voicechat.gui.GameProfileUtils;
import io.github.singlerr.semaphore.interactors.access.database.EntityType;
import io.github.singlerr.semaphore.interactors.admin.controller.CallConnectionController;
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController;
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.CallStateQuery;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableCallConnection;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.policy.PolicyConstants;
import io.github.singlerr.semaphore.policy.dfa.PlayerState;
import java.awt.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;

public final class GuiMutableEntityEntry implements GuiListExtended.IGuiListEntry {
    private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();

    private final EntityController entityController;
    private final CallConnectionController callConnectionController;
    private final CallStateController stateController;

    @Setter
    @Getter
    private PresentableEntity entity;

    @Setter
    private PresentableCallConnection currentConnection;

    private boolean selected;

    private final GuiButton btnDeleteEntity;
    private final GuiButton btnResetState;

    private final GuiTextField txtState;
    private final GuiButton btnSetState;

    private Minecraft mc;

    public GuiMutableEntityEntry(
            EntityController entityController,
            CallConnectionController callConnectionController,
            CallStateController stateController,
            PresentableEntity entity) {
        this.mc = Minecraft.getMinecraft();
        this.entityController = entityController;
        this.callConnectionController = callConnectionController;
        this.stateController = stateController;
        this.entity = entity;

        this.btnDeleteEntity = new GuiButton(0, 0, 0, 50, 20, "Delete");
        this.btnResetState = new GuiButton(1, 0, 0, 50, 20, "Reset");

        this.txtState = new GuiTextField(2, mc.fontRenderer, 0, 0, 50, 20);
        this.btnSetState = new GuiButton(3, 0, 0, 50, 20, "Set State");
    }

    @Override
    public void updatePosition(int slotIndex, int x, int y, float partialTicks) {}

    @Override
    public void drawEntry(
            int slotIndex,
            int x,
            int y,
            int listWidth,
            int slotHeight,
            int mouseX,
            int mouseY,
            boolean isSelected,
            float partialTicks) {
        this.selected = isSelected;

        NetworkPlayerInfo info = mc.getConnection().getPlayerInfo(entity.getId());

        FontRenderer fontRenderer = mc.fontRenderer;
        Gui.drawRect(x, y, x + listWidth, y + slotHeight, Color.GRAY.getRGB());

        this.drawHead(entity.getId(), x + 5, y + 3, slotHeight - 5, slotHeight - 5, partialTicks);

        PlayerState state = PolicyConstants.STATE_NFA.decode(entity.getState().getStateId());

        String displayName = info.getDisplayName() != null
                ? info.getDisplayName().getFormattedText()
                : info.getGameProfile().getName();

        fontRenderer.drawStringWithShadow(displayName, x + slotHeight + 5, y + 5, Color.WHITE.getRGB());
        fontRenderer.drawStringWithShadow(
                state.name(), x + slotHeight + 5, y + 5 + fontRenderer.FONT_HEIGHT + 3, Color.WHITE.getRGB());

        btnDeleteEntity.x = x + slotHeight + 5;
        btnDeleteEntity.y = y + 5 + fontRenderer.FONT_HEIGHT * 2 + 6;

        btnResetState.x = btnDeleteEntity.x + 2 + btnDeleteEntity.width;
        btnResetState.y = y + 5;

        txtState.x = btnResetState.x + 2 + btnResetState.width;
        txtState.y = btnResetState.y;

        btnSetState.x = txtState.x;
        btnSetState.y = txtState.y + 2 + txtState.height;

        btnDeleteEntity.drawButton(mc, mouseX, mouseY, partialTicks);
        btnResetState.drawButton(mc, mouseX, mouseY, partialTicks);
        btnSetState.drawButton(mc, mouseX, mouseY, partialTicks);
        txtState.drawTextBox();
    }

    private void drawHead(UUID playerId, int x, int y, int width, int height, float partialTicks) {
        mc.getTextureManager().bindTexture(GameProfileUtils.getSkin(playerId));
        Gui.drawScaledCustomSizeModalRect(x, y, 8.0F, 8.0F, 8, 8, width, height, 64.0F, 64.0F);
        GlStateManager.enableBlend();
        Gui.drawScaledCustomSizeModalRect(x, y, 40.0F, 8.0F, 8, 8, width, height, 64.0F, 64.0F);
        GlStateManager.disableBlend();
    }

    @Override
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        txtState.mouseClicked(mouseX, mouseY, mouseEvent);
        if (btnDeleteEntity.mousePressed(mc, mouseX, mouseY)) {
            deleteEntity();
            return false;
        }

        if (btnResetState.mousePressed(mc, mouseX, mouseY)) {
            resetEntityState();
        }

        return false;
    }

    private void deleteEntity() {
        entityController.deleteEntity(new EntityQuery.DeleteEntity(entity.getId()));
    }

    private void resetEntityState() {
        entityController.updateEntity(new EntityQuery.UpdateEntity(
                entity.getId(),
                new EntityQuery.State(
                        entity.getState().getStateId(),
                        entity.getState().getMissCallCount(),
                        entity.getState().getEntityType() == EntityType.PLAYER
                                ? io.github.singlerr.semaphore.interactors.admin.controller.data.EntityType.PLAYER
                                : io.github.singlerr.semaphore.interactors.admin.controller.data.EntityType
                                        .PHONE_BOX)));
        if (currentConnection != null)
            stateController.closeCall(new CallStateQuery.CloseCallById(currentConnection.getId()));
    }

    public boolean isSelected() {
        return selected;
    }

    public void keyTyped(char typedChar, int keyCode) {
        this.txtState.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}
}
