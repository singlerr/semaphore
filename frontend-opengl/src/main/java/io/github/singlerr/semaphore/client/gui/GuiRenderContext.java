/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.gui;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuiRenderContext {

    private int mouseX;
    private int mouseY;
    private float partialTicks;
}
