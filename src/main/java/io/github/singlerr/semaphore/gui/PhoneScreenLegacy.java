/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui;

import de.maxhenkel.voicechat.gui.widgets.IngameListScreenBase;
import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.gui.list.AddressList;
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

@Log4j2
public class PhoneScreenLegacy extends IngameListScreenBase {

    private static final ResourceLocation FRAME_ALL = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame.png")
            .build();

    // 722 * 1053
    private static final ResourceLocation FRAME_TOP = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame_top.png")
            .build();

    private static final ResourceLocation FRAME_TOP_L = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame_tl.png")
            .build();

    private static final ResourceLocation FRAME_TOP_R = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame_tr.png")
            .build();
    private static final ResourceLocation FRAME_BOTTOM = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame_bottom.png")
            .build();
    private static final ResourceLocation FRAME_BOTTOM_L = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame_bl.png")
            .build();
    private static final ResourceLocation FRAME_BOTTOM_R = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame_br.png")
            .build();
    private static final ResourceLocation FRAME_MIDDLE_R = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame_mr.png")
            .build();
    private static final ResourceLocation FRAME_MIDDLE_L = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame_ml.png")
            .build();

    private static final int CORNER_WIDTH = 64;
    private static final int CORNER_HEIGHT = 64;

    private static final int SLOT_HEIGHT = 64;

    private AddressList addressList;

    private int frameWidth;
    private int frameHeight;

    private double slide;

    public PhoneScreenLegacy() {
        super(new TextComponentTranslation("gui.phonescreen.title"), 500, 200);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = 5;
        this.guiTop = 30;
        this.frameWidth = (int) (this.width * (double) 1 / 7);
        this.frameHeight = (int) (this.height * 0.7);

        this.addressList = new AddressList(
                this,
                this.guiLeft + 64,
                frameWidth + CORNER_WIDTH,
                frameHeight,
                this.guiTop + CORNER_HEIGHT - 16,
                SLOT_HEIGHT);
        setList(this.addressList);
        this.slide = 10;
    }

    @Override
    public void renderForeground(int mouseX, int mouseY, float delta) {}

    @Override
    public void drawDefaultBackground() {}

    @Override
    public void renderBackground(int mouseX, int mouseY, float delta) {
        super.renderBackground(mouseX, mouseY, delta);

        TextureManager t = mc.getTextureManager();

        // TOP
        t.bindTexture(FRAME_TOP_L);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 256 - 100, 0, 100, CORNER_HEIGHT);
        t.bindTexture(FRAME_TOP);
        drawTexturedModalRect(this.guiLeft + 100, this.guiTop, 0, 0, frameWidth, CORNER_HEIGHT);
        t.bindTexture(FRAME_TOP_R);
        drawTexturedModalRect(this.guiLeft + 100 + frameWidth, this.guiTop, 0, 0, CORNER_WIDTH, CORNER_HEIGHT);
        // MIDDLE
        t.bindTexture(FRAME_MIDDLE_L);
        drawTexturedModalRect(
                this.guiLeft + 36,
                this.guiTop + CORNER_HEIGHT,
                256 - CORNER_WIDTH,
                0,
                CORNER_WIDTH,
                frameHeight - CORNER_HEIGHT);
        t.bindTexture(FRAME_MIDDLE_R);
        drawTexturedModalRect(
                this.guiLeft + 100 + frameWidth + 20,
                this.guiTop + CORNER_HEIGHT,
                20,
                0,
                CORNER_WIDTH,
                (frameHeight - CORNER_HEIGHT) / 2);
        drawTexturedModalRect(
                this.guiLeft + 100 + frameWidth + 20,
                this.guiTop + CORNER_HEIGHT + (frameHeight - CORNER_HEIGHT) / 2,
                20,
                60,
                CORNER_WIDTH,
                (frameHeight - CORNER_HEIGHT) / 2 + 1);
        // BOTTOM
        t.bindTexture(FRAME_BOTTOM_L);
        drawTexturedModalRect(
                this.guiLeft, this.guiTop + frameHeight, 256 - 100, 256 - CORNER_HEIGHT, 100, CORNER_HEIGHT);
        t.bindTexture(FRAME_BOTTOM);
        drawTexturedModalRect(
                this.guiLeft + 100, this.guiTop + frameHeight, 0, 256 - CORNER_HEIGHT, frameWidth, CORNER_HEIGHT);
        t.bindTexture(FRAME_BOTTOM_R);
        drawTexturedModalRect(
                this.guiLeft + 100 + frameWidth,
                this.guiTop + frameHeight,
                0,
                256 - CORNER_HEIGHT,
                CORNER_WIDTH,
                CORNER_HEIGHT);

        GlStateManager.disableAlpha();
    }
}
