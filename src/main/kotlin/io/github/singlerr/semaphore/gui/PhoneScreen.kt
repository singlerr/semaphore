/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.gui.components.*
import io.github.singlerr.semaphore.registries.ClientRegistries
import io.github.singlerr.semaphore.sound.ClientSoundHandler
import io.github.singlerr.semaphore.state.player.PlayerContext
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder
import io.github.singlerr.semaphore.utils.Resources
import io.github.singlerr.semaphore.utils.asImageAsync
import io.github.singlerr.semaphore.utils.asImageAsyncNullable
import net.minecraft.server.MinecraftServer

class PhoneScreen(private val playerState: PlayerContext) :
    WindowScreen(ElementaVersion.V5, drawDefaultBackground = false) {

    private val frame: UIComponent

    private val rootComponent: UIComponent

    private val settingsScreen: UISettings
    private val addressScreen: UIAddressList

    private var inCallScreen: UIInComingCall? = null
    private var outCallScreen: UIOutComingCall? = null

    private var inSettings = false

    init {

        frame =
            UIImage(Resources.PHONE_FRAME.asImageAsync()).constrain {
                x = 0.pixels()
                y = 5.percent()
                width = 40.percent()
                height = 80.percent()
            } childOf window

        val backgroundImage =
            ResourceLocationBuilder.builder()
                .namespace(Semaphore.MOD_ID)
                .append("textures")
                .append("gui")
                .append("backgrounds")
                .append("${playerState.owner}.png")
                .build()
                .asImageAsyncNullable()

        val backgroundX = 27.percent() boundTo frame
        val backgroundY = 8.percent() boundTo frame
        val backgroundWidth = 46.percent() boundTo frame
        val backgroundHeight = 84.percent() boundTo frame
        val container =
            if (backgroundImage != null) {
                UIImage(backgroundImage).constrain {
                    x = backgroundX
                    y = backgroundY

                    width = backgroundWidth
                    height = backgroundHeight
                } childOf frame
            } else {
                UIBlock().constrain {
                    x = backgroundX
                    y = backgroundY

                    width = backgroundWidth
                    height = backgroundHeight
                } childOf frame
            }

        rootComponent = container
        addressScreen = UIAddressList(container, playerState, mutableListOf())
        settingsScreen =
            UISettings(container, playerState, mutableListOf()).constrain {
                x = 0.pixels() boundTo container
                y = 0.pixels() boundTo container

                width = 100.percent() boundTo container
                height = 100.percent() boundTo container
            } childOf container

        settingsScreen.hide(true)

        val settingsBtn =
            UIImage(Resources.SETTINGS_ICON.asImageAsync()).constrain {
                x = 10.pixels() boundTo container
                y = 20.pixels(true) boundTo frame

                width = 10.pixels()
                height = 10.pixels()
            } childOf frame

        settingsBtn.onMouseClick {
            if (inSettings) {
                addressScreen.component.unhide()
                settingsScreen.hide(true)
            } else {
                addressScreen.component.hide(true)
                settingsScreen.unhide()
            }

            inSettings = !inSettings
        }

        frame.onMouseClick { ClientSoundHandler.playTouchSound() }
        Inspector(window).constrain {
            x = 10.pixels(true)
            y = 10.pixels(true)
        } childOf window
    }

    fun addOrUpdatePlayerState(state: PlayerContext) {
        Window.enqueueRenderOperation {
            addressScreen.addOrUpdatePlayerState(state)
            settingsScreen.addPlayerState(state)
        }
    }

    fun removePlayerState(state: PlayerContext) {
        Window.enqueueRenderOperation {
            addressScreen.addOrUpdatePlayerState(state)
            settingsScreen.addPlayerState(state)
        }
    }

    fun clearPlayerStates(states: Iterable<PlayerContext>) {
        Window.enqueueRenderOperation {
            addressScreen.component.clearChildren()
            settingsScreen.volumeSettings.component.clearChildren()

            states.forEach { s ->
                addressScreen.addOrUpdatePlayerState(s)
                settingsScreen.addPlayerState(s)
            }
        }
    }

    private fun removeCallingScreen() {
        Window.enqueueRenderOperation {
            outCallScreen?.let {
                it.hide(true)
                rootComponent.removeChild(it)
            }
            outCallScreen = null
        }
    }

    private fun removeInCallScreen() {
        Window.enqueueRenderOperation {
            inCallScreen?.let {
                it.hide(true)
                rootComponent.removeChild(it)
            }
            inCallScreen = null

            outCallScreen?.let {
                it.hide(true)
                rootComponent.removeChild(it)
            }
            outCallScreen = null
        }
    }

    fun callEstablished() {
        removeCallingScreen()
    }

    fun callClosed() {
        removeInCallScreen()
    }

    fun receivingCall(state: PlayerContext) {
        Window.enqueueRenderOperation {
            val window = ClientRegistries.getOrCreateNotificationWindow(state.owner)
            window.playTranslate()
            inCallScreen = UIInComingCall(rootComponent, playerState, state.owner)
            rootComponent.addChild(inCallScreen!!)
        }
    }
}
