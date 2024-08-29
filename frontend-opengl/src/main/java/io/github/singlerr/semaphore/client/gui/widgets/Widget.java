/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.gui.widgets;

import io.github.singlerr.semaphore.client.gui.GuiRenderContext;
import io.github.singlerr.semaphore.client.gui.events.GuiWidgetClickEvent;
import io.github.singlerr.semaphore.client.gui.events.GuiWidgetEnterEvent;
import io.github.singlerr.semaphore.client.gui.events.GuiWidgetLeaveEvent;
import io.github.singlerr.semaphore.client.gui.inputs.GuiKeyboardInput;
import io.github.singlerr.semaphore.client.gui.inputs.GuiMouseInput;

public abstract class Widget {

    public abstract void draw(GuiRenderContext context);

    public boolean onEnter(GuiWidgetEnterEvent event) {
        return false;
    }

    public boolean onLeave(GuiWidgetLeaveEvent event) {
        return false;
    }

    public boolean onClick(GuiWidgetClickEvent event) {
        return false;
    }

    public boolean onKeyboardInput(GuiKeyboardInput input) {
        return false;
    }

    public boolean onMouseInput(GuiMouseInput input) {
        return false;
    }
}
