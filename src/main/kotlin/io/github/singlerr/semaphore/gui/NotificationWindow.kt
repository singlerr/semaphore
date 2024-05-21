/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui

import de.maxhenkel.voicechat.gui.GameProfileUtils
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.gui.components.UIPlayerSkull
import io.github.singlerr.semaphore.registries.ClientRegistries
import io.github.singlerr.semaphore.utils.Resources
import io.github.singlerr.semaphore.utils.asImageAsync
import io.github.singlerr.semaphore.utils.primaryBackground
import net.minecraft.client.resources.I18n
import java.awt.Color
import java.util.UUID
import org.lwjgl.input.Keyboard
import java.net.URL

class NotificationWindow(playerId: UUID) {

    private var hidden: Boolean = true

    val handle: Window

    val container: UIComponent

    private val xPos = -150
    init {

        handle = Window(ElementaVersion.V5)

        val backgroundColor = primaryBackground()
        container =
            UIBlock(backgroundColor).constrain {
                x = xPos.pixels(true)
                y = 10.pixels()

                width = ChildBasedSizeConstraint(padding = 2f)
                height = ChildBasedMaxSizeConstraint() + 5.pixels()
            } childOf handle
        val playerSkull =
            UIImage.ofURL(URL("https://mc-heads.net/head/$playerId")).constrain {
                x = 5.pixels()
                y = CenterConstraint()

                width = 15.pixels()
                height = ImageAspectConstraint()
            } childOf container

        val acceptIcon =
            UIImage(Resources.ICON_CALL_ACCEPT.build().asImageAsync()).constrain {
                x = SiblingConstraint(3f) boundTo playerSkull
                y = CenterConstraint() boundTo playerSkull

                width = 10.pixels()
                height = ImageAspectConstraint()
            } childOf container

        val acceptText =
            UIWrappedText(I18n.format("gui.notification.accept", Keyboard.getKeyName(ClientRegistries.KEY_ACCEPT_CALL.keyCode))).constrain {
                x = SiblingConstraint(2f) boundTo acceptIcon
                y = ((CenterConstraint()) boundTo acceptIcon) - 2.pixels()

//                width = 5.pixels()
                height = 5.pixels()
            } childOf container

        val denyIcon =
            UIImage(Resources.ICON_CALL_DENY.build().asImageAsync()).constrain {
                x = SiblingConstraint(3f) boundTo acceptText
                y = CenterConstraint() boundTo playerSkull

                width = 10.pixels()
                height = ImageAspectConstraint()
            } childOf container

        val denyText =
            UIWrappedText(I18n.format("gui.notification.deny", Keyboard.getKeyName(ClientRegistries.KEY_DENY_CALL.keyCode))).constrain {
                x = SiblingConstraint(2f) boundTo denyIcon
                y = ((CenterConstraint()) boundTo denyIcon) - 2.pixels()

//                width = 5.pixels()
                height = 5.pixels()
            } childOf container

    }

    fun onShow() {
        container.animate { setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels(true)) }
    }

    fun onHide(onComplete: Runnable){
        container.animate { setXAnimation(Animations.OUT_EXP, 0.5f, (-150).pixels(true))
        onComplete {
            onComplete.run()
        }}
    }

}
