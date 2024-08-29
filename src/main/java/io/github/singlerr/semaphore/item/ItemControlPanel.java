/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.item;

import io.github.singlerr.semaphore.Semaphore;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public final class ItemControlPanel extends Item {

    public ItemControlPanel() {
        super();
        setRegistryName(new ResourceLocation(Semaphore.MOD_ID, "item_control_panel"));
        setTranslationKey(Semaphore.MOD_ID + ".control_panel");
        setMaxStackSize(1);
    }
}
