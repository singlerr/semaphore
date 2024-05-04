/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.list;

import de.maxhenkel.voicechat.gui.GameProfileUtils;
import de.maxhenkel.voicechat.gui.VoiceChatScreen;
import de.maxhenkel.voicechat.gui.widgets.IngameListScreenBase;
import de.maxhenkel.voicechat.gui.widgets.ListScreenEntryBase;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import de.maxhenkel.voicechat.voice.client.ClientVoicechat;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;

import java.awt.*;
import java.util.UUID;

public class AddressListEntry extends ListScreenEntryBase {

    @Getter
    private final UUID id;

    @Getter
    private final PlayerContext state;

    private final int BG_COLOR = Color.WHITE.getRGB();

    private final int NAME_COLOR = Color.BLUE.getRGB();

    private final IngameListScreenBase parent;

    private final int PADDING = 4;

    private final int start;

    public AddressListEntry(IngameListScreenBase parent,int start, UUID id, PlayerContext state) {
        this.parent = parent;
        this.id = id;
        this.state = state;
        this.start = start;
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected, partialTicks);
        GlStateManager.pushMatrix();

        GuiScreen.drawRect(x, y, x + listWidth, y + slotHeight, BG_COLOR);

        int outlineSize = slotHeight - PADDING * 2;

        GlStateManager.translate(x + PADDING, y + PADDING, 0D);
        float scale = outlineSize / 10F;
        GlStateManager.scale(scale, scale, scale);

        GlStateManager.color(1F, 1F, 1F, 1F);

        TextureManager t = Minecraft.getMinecraft().getTextureManager();
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        t.bindTexture(GameProfileUtils.getSkin(id));
        Gui.drawScaledCustomSizeModalRect(1, 1, 8F, 8F, 8, 8, 8, 8, 64F, 64F);
        GlStateManager.enableBlend();
        Gui.drawScaledCustomSizeModalRect(1, 1, 40F, 8F, 8, 8, 8, 8, 64F, 64F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        fontRenderer.drawString(fontRenderer.trimStringToWidth(id.toString(), listWidth), x + PADDING + outlineSize + PADDING, y + slotHeight / 2 - fontRenderer.FONT_HEIGHT / 2, NAME_COLOR);
    }
}
