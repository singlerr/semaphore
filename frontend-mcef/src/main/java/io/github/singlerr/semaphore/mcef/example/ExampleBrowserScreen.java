/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mcef.example;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.api.API;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.MCEFApi;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ExampleBrowserScreen extends GuiScreen {

    IBrowser browser = null;

    private String urlToLoad = null;

    private static final String YT_REGEX1 = "^https?://(?:www\\.)?youtube\\.com/watch\\?v=([a-zA-Z0-9_\\-]+)$";
    private static final String YT_REGEX2 = "^https?://(?:www\\.)?youtu\\.be/([a-zA-Z0-9_\\-]+)$";
    private static final String YT_REGEX3 = "^https?://(?:www\\.)?youtube\\.com/embed/([a-zA-Z0-9_\\-]+)(\\?.+)?$";

    public ExampleBrowserScreen() {
        urlToLoad = "http://localhost:3000";
    }

    public ExampleBrowserScreen(String url) {
        urlToLoad = (url == null) ? MCEF.HOME_PAGE : url;
    }

    private int left, right, top, bottom;

    @Override
    public void initGui() {
        ExampleMod.INSTANCE.hudBrowser = null;

        if (browser == null) {
            // Grab the API and make sure it isn't null.
            API api = MCEFApi.getAPI();
            if (api == null) return;

            // Create a browser and resize it to fit the screen
            browser = api.createBrowser((urlToLoad == null) ? MCEF.HOME_PAGE : urlToLoad, false);
            urlToLoad = null;
        }

        // Resize the browser if window size changed
        if (browser != null) {
            ScaledResolution resolution = new ScaledResolution(mc);
            top = 10;
            bottom = resolution.getScaledHeight() - 50;
            left = 10;
            right = 60;
            int width = right - left;
            int height = bottom - top;

            browser.resize(width, height);
        }

        // Create GUI
        Keyboard.enableRepeatEvents(true);
    }

    public int scaleY(int y) {
        double sy = ((double) y) / ((double) height) * ((double) mc.displayHeight);
        return (int) sy;
    }

    public void loadURL(String url) {
        if (browser == null) urlToLoad = url;
        else browser.loadURL(url);
    }

    @Override
    public void updateScreen() {
        if (urlToLoad != null && browser != null) {
            browser.loadURL(urlToLoad);
            urlToLoad = null;
        }
    }

    @Override
    public void drawScreen(int i1, int i2, float f) {
        // Renders the browser if itsn't null
        if (browser != null) {
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            browser.draw(left, top, right, bottom); // Don't forget to flip Y axis.
            GlStateManager.enableDepth();
        }
    }

    @Override
    public void onGuiClosed() {
        // Make sure to close the browser when you don't need it anymore.
        if (!ExampleMod.INSTANCE.hasBackup() && browser != null) browser.close();

        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void handleInput() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                mc.displayGuiScreen(null);
                return;
            }

            boolean pressed = Keyboard.getEventKeyState();
            char key = Keyboard.getEventCharacter();
            int num = Keyboard.getEventKey();

            if (browser != null) { // Inject events into browser
                if (pressed) browser.injectKeyPressedByKeyCode(num, key, 0);
                else browser.injectKeyReleasedByKeyCode(num, key, 0);

                if (key != 0) browser.injectKeyTyped(key, 0);
            }
        }

        while (Mouse.next()) {
            int btn = Mouse.getEventButton();
            boolean pressed = Mouse.getEventButtonState();
            int sx = Mouse.getEventX();
            int sy = Mouse.getEventY();
            int wheel = Mouse.getEventDWheel();

            if (browser != null) { // Inject events into browser. TODO: Handle mods & leaving.
                int y = mc.displayHeight - sy - scaleY(20); // Don't forget to flip Y axis.

                if (wheel != 0) browser.injectMouseWheel(sx, y, 0, 1, wheel);
                else if (btn == -1) browser.injectMouseMove(sx, y, 0, y < 0);
                else browser.injectMouseButton(sx, y, 0, btn + 1, pressed, 1);
            }

            if (pressed) { // Forward events to GUI.
                int x = sx * width / mc.displayWidth;
                int y = height - (sy * height / mc.displayHeight) - 1;

                try {
                    mouseClicked(x, y, btn);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    // Called by ExampleMod when the current browser's URL changes.
    public void onUrlChanged(IBrowser b, String nurl) {}

    // Handle button clicks
    @Override
    protected void actionPerformed(GuiButton src) {
        if (browser == null) return;

        if (src.id == 0) browser.goBack();
        else if (src.id == 1) browser.goForward();
        else if (src.id == 2) {
        } else if (src.id == 3) {
            ExampleMod.INSTANCE.setBackup(this);
            mc.displayGuiScreen(null);
        } else if (src.id == 4) {
            String loc = browser.getURL();
            String vId = null;
            boolean redo = false;

            if (loc.matches(YT_REGEX1)) vId = loc.replaceFirst(YT_REGEX1, "$1");
            else if (loc.matches(YT_REGEX2)) vId = loc.replaceFirst(YT_REGEX2, "$1");
            else if (loc.matches(YT_REGEX3)) redo = true;

            if (vId != null || redo) {
                ExampleMod.INSTANCE.setBackup(this);
                mc.displayGuiScreen(new ScreenCfg(browser, vId));
            }
        }
    }
}
