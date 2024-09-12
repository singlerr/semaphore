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
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController;
import io.github.singlerr.semaphore.interactors.caller.controller.data.CallRequest;
import io.github.singlerr.semaphore.policy.PolicyConstants;
import io.github.singlerr.semaphore.policy.dfa.PlayerState;
import io.github.singlerr.semaphore.utils.Utils;
import java.awt.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;

public final class GuiEntityEntry implements GuiListExtended.IGuiListEntry {
    private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();

    private final EntityController entityController;
    private final CallConnectionController callConnectionController;
    private final CallRequestController requestController;
    private final CallStateController stateController;

    @Setter
    @Getter
    private PresentableEntity entity;

    @Setter
    private PresentableCallConnection currentConnection;

    private boolean selected;

    private final GuiButton btnDeleteEntity;
    private final GuiButton btnCallEntity;

    private Minecraft mc;

    public GuiEntityEntry(
            EntityController entityController,
            CallConnectionController callConnectionController,
            CallStateController callStateController,
            CallRequestController requestController,
            PresentableEntity entity) {
        this.entityController = entityController;
        this.callConnectionController = callConnectionController;
        this.stateController = callStateController;
        this.requestController = requestController;
        this.entity = entity;

        this.btnDeleteEntity = new GuiButton(0, 0, 0, 50, 20, "Delete");
        this.btnCallEntity = new GuiButton(1, 0, 0, 100, 20, "Call");

        this.mc = Minecraft.getMinecraft();
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
        boolean isPhoneBox = entity.getState().getEntityType() == EntityType.PHONE_BOX;

        FontRenderer fontRenderer = mc.fontRenderer;
        Gui.drawRect(x, y, x + listWidth, y + slotHeight, Color.GRAY.getRGB());

        if (!isPhoneBox) this.drawHead(entity.getId(), x + 5, y + 3, slotHeight - 5, slotHeight - 5, partialTicks);

        PlayerState state = PolicyConstants.STATE_NFA.decode(entity.getState().getStateId());

        BlockPos pos = Utils.fromUUID(entity.getId());
        String text = String.format("PhoneBox(x=%d,y=%d,z=%d)", pos.getX(), pos.getY(), pos.getZ());

        fontRenderer.drawStringWithShadow(text, x + slotHeight + 5, y + 5, Color.WHITE.getRGB());
        fontRenderer.drawStringWithShadow(
                state.name(), x + slotHeight + 5, y + 5 + fontRenderer.FONT_HEIGHT + 3, Color.WHITE.getRGB());

        btnDeleteEntity.x = x + slotHeight + 5;
        btnDeleteEntity.y = y + 5 + fontRenderer.FONT_HEIGHT * 2 + 6;
        btnCallEntity.x = btnDeleteEntity.x + btnDeleteEntity.width + 2;
        btnCallEntity.y = btnDeleteEntity.y;

        btnDeleteEntity.drawButton(mc, mouseX, mouseY, partialTicks);
        btnCallEntity.drawButton(mc, mouseX, mouseY, partialTicks);

        btnCallEntity.displayString = state != PlayerState.DEFAULT ? "Close Call" : "Request Call";
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
        btnCallEntity.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        if (btnDeleteEntity.mousePressed(mc, mouseX, mouseY)) {
            deleteEntity();
            return false;
        }

        if (btnCallEntity.mousePressed(mc, mouseX, mouseY)) {
            PlayerState state =
                    PolicyConstants.STATE_NFA.decode(entity.getState().getStateId());
            if (state != PlayerState.DEFAULT) {
                closeCall();
                return false;
            }
            callEntity();
        }

        return false;
    }

    private void deleteEntity() {
        entityController.deleteEntity(new EntityQuery.DeleteEntity(entity.getId()));
    }

    private void callEntity() {
        requestController.request(new CallRequest(mc.player.getUniqueID(), entity.getId()));
    }

    private void closeCall() {
        if (currentConnection != null) {
            stateController.closeCall(new CallStateQuery.CloseCallById(currentConnection.getId()));
        } else {
            // Call twice to stop call
            callEntity();
        }
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}
}
