/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.state.player.PlayerContext

class UIAddressList(
    parent: UIComponent,
    private val ownerState: PlayerContext,
    states: List<PlayerContext>
) {

    val component: UIComponent

    init {
        component =
            ScrollComponent(innerPadding = 2f, scrollDirection = ScrollComponent.Direction.Vertical)
                .constrain {
                    x = CenterConstraint()
                    y = 0.pixels() boundTo parent

                    width = 100.percent() boundTo parent
                    height = 100.percent() boundTo parent
                } childOf parent

        states
            .filter { ctx -> ctx.owner != ownerState.owner }
            .forEach { ctx -> component.addChild(UIAddress(ownerState, ctx)) }
    }
    fun removePlayerState(state: PlayerContext) {
        val children = component.childrenOfType<UIAddress>()
        val child = children.find { c -> c.currentState.owner == state.owner }
        child?.let { component.removeChild(it) }
    }

    fun addOrUpdatePlayerState(state: PlayerContext) {
        val children = component.childrenOfType<UIAddress>()
        var childExist = false
        children.forEach { child ->
            if (child.currentState.owner == state.owner) {
                childExist = true
                child.update(ownerState, state)
            }
        }

        if (!childExist) {
            component.addChild(UIAddress(ownerState, state))
        }
    }
}
