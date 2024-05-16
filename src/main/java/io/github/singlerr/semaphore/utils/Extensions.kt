/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils

import gg.essential.elementa.impl.dom4j.Element
import gg.essential.elementa.svg.data.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import javax.imageio.ImageIO
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

fun SVGCircle.Companion.fromElement(element: Element): SVGCircle {
    return SVGCircle(
        element.attributeValue("cx").toFloat(),
        element.attributeValue("cy").toFloat(),
        element.attributeValue("r").toFloat())
}

fun SVGRect.Companion.fromElement(element: Element): SVGRect {
    val topLeft =
        Point(element.attributeValue("x").toFloat(), element.attributeValue("y").toFloat())
    val width = element.attributeValue("width").toFloat()
    val height = element.attributeValue("height").toFloat()

    return SVGRect(
        topLeft,
        topLeft.copy(x = topLeft.x + width),
        topLeft.copy(x = topLeft.x + width, y = topLeft.y + height),
        topLeft.copy(y = topLeft.y + height))
}

fun SVGLine.Companion.fromElement(element: Element): SVGLine {
    return SVGLine(
        Point(element.attributeValue("x1").toFloat(), element.attributeValue("y1").toFloat()),
        Point(element.attributeValue("x2").toFloat(), element.attributeValue("y2").toFloat()))
}

fun SVGPolyline.Companion.fromElement(element: Element): SVGPolyline {
    val points =
        element
            .attributeValue("points")
            .split(" ")
            .zipWithNext()
            .map { Point(it.first.toFloat(), it.second.toFloat()) }
            .filterIndexed { index, _ -> index % 2 == 0 }

    return SVGPolyline(points)
}

fun ResourceLocation.asInputStream(): InputStream {
    return Minecraft.getMinecraft().resourceManager.getResource(this).inputStream
}

private val imageCache = ConcurrentHashMap<ResourceLocation, BufferedImage>()

fun ResourceLocation.asImageAsync(): CompletableFuture<BufferedImage> {
    if (imageCache.containsKey(this)) {
        return CompletableFuture.completedFuture(imageCache[this])
    }
    return CompletableFuture.supplyAsync { ImageIO.read(asInputStream()) }
        .thenApplyAsync { img ->
            imageCache[this] = img
            img
        }
}

fun ResourceLocation.asImageAsyncNullable(): CompletableFuture<BufferedImage>? {
    if (imageCache.containsKey(this)) {
        return CompletableFuture.completedFuture(imageCache[this])
    }
    val input: InputStream
    try {
        input = asInputStream()
    } catch (_: Exception) {
        return null
    }
    return CompletableFuture.supplyAsync { ImageIO.read(input) }
        .thenApplyAsync { img ->
            imageCache[this] = img
            img
        }
}

fun ResourceLocation.asImageAsync(
    preApply: Function<BufferedImage, BufferedImage>
): CompletableFuture<BufferedImage> {
    if (imageCache.containsKey(this)) {
        return CompletableFuture.completedFuture(imageCache[this])
    }
    return CompletableFuture.supplyAsync { ImageIO.read(asInputStream()) }
        .thenApplyAsync(preApply)
        .thenApplyAsync { img ->
            imageCache[this] = img
            img
        }
}

fun ResourceLocation.exists(): Boolean {
    return try {
        Minecraft.getMinecraft().resourceManager.getResource(this)
        true
    } catch (e: Exception) {
        false
    }
}

fun primaryBackground(): Color {
    return Color(238, 238, 238)
}
