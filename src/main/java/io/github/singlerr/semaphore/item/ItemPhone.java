/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.item;

import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public final class ItemPhone extends Item {

    public ItemPhone() {
        super();

        setCreativeTab(CreativeTabs.MISC);
        setRegistryName(ResourceLocationBuilder.builder()
                .namespace(Semaphore.MOD_ID)
                .append("phone_item")
                .build());
        setTranslationKey("semaphore.phoneitem");
    }
}
