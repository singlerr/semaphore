/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.item;

import io.github.singlerr.semaphore.Semaphore;
import net.minecraft.item.Item;

public final class ItemPhone extends Item {

    public ItemPhone() {
        super();
        setRegistryName("item_phone");
        setTranslationKey(Semaphore.MOD_ID + ".itemPhone");
        setMaxStackSize(1);
    }
}
