/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.item;

import io.github.singlerr.semaphore.Semaphore;
import net.minecraft.item.Item;

public final class ItemControlPanel extends Item {

    public ItemControlPanel() {
        super();
        setRegistryName("control_panel");
        setTranslationKey(Semaphore.MOD_ID + ".control_panel");
        setMaxStackSize(1);
    }
}
