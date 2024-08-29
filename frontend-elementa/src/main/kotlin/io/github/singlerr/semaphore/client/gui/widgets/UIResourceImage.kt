package io.github.singlerr.semaphore.client.gui.widgets

import gg.essential.elementa.components.UIImage
import java.util.concurrent.CompletableFuture
import net.minecraft.util.ResourceLocation

class UIResourceImage(resourceLocation: ResourceLocation) :
    UIImage(CompletableFuture.supplyAsync({ loadResource(resourceLocation) }))
