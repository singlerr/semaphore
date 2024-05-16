/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui

import de.maxhenkel.voicechat.gui.GameProfileUtils
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.gui.components.UIPlayerSkull
import io.github.singlerr.semaphore.registries.ClientRegistries
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder
import io.github.singlerr.semaphore.utils.asImageAsync
import java.awt.Color
import java.util.UUID
import org.lwjgl.input.Keyboard

class NotificationWindow(playerId: UUID) {

  companion object {
    private val CALL_ACCEPT_ICON =
        ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_call.png")
            .build()
  }

  var hidden: Boolean = true

  val handle: Window

  init {

    handle = Window(ElementaVersion.V5)

    val backgroundColor = Color(43, 45, 48)

    val container =
        UIBlock(backgroundColor).constrain {
          x = 0.pixels(true)
          y = 10.pixels()

          width = 150.pixels()
          height = 40.pixels()
        } childOf handle

    val skin = GameProfileUtils.getSkin(playerId)
    val playerSkull =
        UIPlayerSkull(skin).constrain {
          x = 5.pixels()
          y = CenterConstraint()

          width = 15.pixels()
          height = ImageAspectConstraint()
        } childOf container

    val acceptIcon =
        UIImage(CALL_ACCEPT_ICON.asImageAsync()).constrain {
          x = SiblingConstraint(1f) boundTo playerSkull
          y = CenterConstraint() boundTo playerSkull

          width = 10.pixels()
          height = ImageAspectConstraint()
        } childOf container

    val acceptText =
        UIText(Keyboard.getKeyName(ClientRegistries.KEY_ACCEPT_CALL.keyCode)).constrain {
          x = SiblingConstraint(1f) boundTo acceptIcon
          y = CenterConstraint() boundTo acceptIcon

          width = 5.pixels()
          height = 5.pixels()
        } childOf container

    val denyIcon =
        UIImage(CALL_ACCEPT_ICON.asImageAsync()).constrain {
          x = SiblingConstraint(1f) boundTo acceptText
          y = CenterConstraint() boundTo playerSkull

          width = 10.pixels()
          height = ImageAspectConstraint()
        } childOf container

    val denyText =
        UIText(Keyboard.getKeyName(ClientRegistries.KEY_DENY_CALL.keyCode)).constrain {
          x = SiblingConstraint(2f) boundTo denyIcon
          y = CenterConstraint() boundTo acceptIcon

          width = 5.pixels()
          height = 5.pixels()
        } childOf container
  }

  fun playTranslate() {
    handle.animate { setXAnimation(Animations.IN_EXP, 2f, 0.pixels(true)) }
  }

  fun hideWindow() {
    handle.hide(true)
    hidden = true
  }

  fun showWindow() {
    hidden = false
    handle.unhide()
  }

  fun draw() {
    handle.draw()
  }
}
