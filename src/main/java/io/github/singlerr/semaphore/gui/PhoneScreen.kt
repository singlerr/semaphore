/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.gui.components.*
import io.github.singlerr.semaphore.network.packets.CallFeedbackPacket
import io.github.singlerr.semaphore.network.packets.CallStatePacket
import io.github.singlerr.semaphore.network.packets.PlayerStatePacket
import io.github.singlerr.semaphore.regisries.CommonRegistries
import io.github.singlerr.semaphore.state.StatePool
import io.github.singlerr.semaphore.state.player.PlayerContext
import io.github.singlerr.semaphore.state.player.PlayerContext.CallState
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder
import io.github.singlerr.semaphore.utils.asImageAsync
import java.util.UUID
import net.minecraft.util.ResourceLocation

class PhoneScreen(private val statePool: StatePool, private val playerId: UUID) :
    WindowScreen(ElementaVersion.V5) {

    companion object {
        private val SETTINGS_ICON =
            ResourceLocationBuilder.builder()
                .namespace(Semaphore.MOD_ID)
                .append("textures")
                .append("gui")
                .append("settings.png")
                .build()

        private val PHONE_FRAME: ResourceLocation =
            ResourceLocationBuilder.builder()
                .namespace(Semaphore.MOD_ID)
                .append("textures")
                .append("gui")
                .append("phone_frame.png")
                .build()
    }

    private val frame: UIComponent

    private val settingsScreen: UISettings
    private val addressScreen: UIAddressList

    private var inCallScreen: UIInCall? = null
    private var outCallScreen: UIOutCall? = null

    private var inSettings = false

    private val ownerState: PlayerContext

    init {

        frame =
            UIImage(PHONE_FRAME.asImageAsync()).constrain {
                x = 0.pixels()
                y = 5.percent()
                width = 40.percent()
                height = 80.percent()
            } childOf window

        val states = statePool.states.map { t -> t.value }.filterIsInstance<PlayerContext>()

        ownerState = statePool.get(playerId, PlayerContext::class.java).get()

        val container =
            UIBlock().constrain {
                x = 30.percent() boundTo frame
                y = 10.pixels() boundTo frame

                width = 40.percent() boundTo frame
                height = 80.percent() boundTo frame
            } childOf frame

        addressScreen = UIAddressList(container, ownerState, states)
        settingsScreen =
            UISettings(container, ownerState, states).constrain {
                x = 0.pixels() boundTo container
                y = 0.pixels() boundTo container

                width = 100.percent() boundTo container
                height = 100.percent() boundTo container
            } childOf container

        settingsScreen.hide(true)

        val settingsBtn =
            UIImage(SETTINGS_ICON.asImageAsync()).constrain {
                x = 10.pixels() boundTo container
                y = 10.pixels(true) boundTo frame

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

        Inspector(window).constrain {
            x = 10.pixels(true)
            y = 10.pixels(true)
        } childOf window
    }

    fun updateCallFeedback(packet: CallFeedbackPacket) {
        if (packet.callee == ownerState.owner) {
            if (packet.callFeedback == PlayerContext.CallFeedback.ACCEPT) {
                ownerState.callState = CallState.IN_CALL
                ownerState.opponent = packet.caller
                CommonRegistries.NETWORK.sendToServer(
                    PlayerStatePacket.builder().id(ownerState.owner).state(ownerState).build())
            }
        } else if (packet.caller == ownerState.owner) {
            if (packet.callFeedback != PlayerContext.CallFeedback.ACCEPT) {
                // TODO("Play denied sound")
                outCallScreen?.hide(true)
            } else {
                ownerState.opponent = packet.callee
                ownerState.callState = CallState.IN_CALL
                CommonRegistries.NETWORK.sendToServer(
                    PlayerStatePacket.builder().id(ownerState.owner).state(ownerState).build())
                outCallScreen?.callAccepted()
            }
        }
    }

    fun updatePlayerState(packet: PlayerStatePacket) {
        CommonRegistries.updatePlayerState(packet)
        addressScreen.update(CommonRegistries.getStatePool())
    }

    fun updateCallState(packet: CallStatePacket) {
        if (packet.callee == playerId) {
            when (packet.action) {
                CallState.CALLING -> {
                    if (ownerState.callState != CallState.IDLE) {
                        CommonRegistries.NETWORK.sendToServer(
                            CallFeedbackPacket.builder()
                                .callFeedback(PlayerContext.CallFeedback.DENY_IN_CALL)
                                .caller(packet.caller)
                                .callee(packet.callee)
                                .build())

                        ownerState.missCalls[packet.caller]?.incrementAndGet()
                    } else {
                        inCallScreen?.unhide()
                    }
                }
                else -> {}
            }
        } else {
            // Update other player state
        }
    }
}
