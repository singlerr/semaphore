/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.item;

import io.github.singlerr.semaphore.Semaphore;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public final class ItemPhone extends Item {

    public ItemPhone() {
        super();
        setRegistryName(new ResourceLocation(Semaphore.MOD_ID, "item_phone"));
        setTranslationKey(Semaphore.MOD_ID + ".item_phone");
        setMaxStackSize(1);
    }
}
