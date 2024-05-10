/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.state.StatePool
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
                    x = 0.pixels() boundTo parent
                    y = 0.pixels() boundTo parent

                    width = 100.percent() boundTo parent
                    height = 100.percent() boundTo parent
                }

        states
            .filter { ctx -> ctx.owner != ownerState.owner }
            .forEach { ctx -> component.addChild(UIAddress(ownerState, ctx)) }
    }

    fun update(ownerState: PlayerContext, targetState: PlayerContext) {
        val target =
            component.children.find { c ->
                c is UIAddress && c.currentState.owner == targetState.owner
            }
        target?.apply { ((this as UIAddress)).update(ownerState, targetState) }
    }

    fun update(statePool: StatePool) {
        val states = statePool.states.filterIsInstance<PlayerContext>().toMutableList()

        val children = component.children.filterIsInstance<UIAddress>().toMutableList()

        children.removeIf { address ->
            val target = states.find { ctx -> ctx.owner == address.currentState.owner }

            target?.apply {
                update(ownerState, this)
                return@removeIf true
            }
            return@removeIf false
        }

        states.forEach { state ->
            val target =
                component.children.find { it is UIAddress && it.currentState.owner == state.owner }
            target?.apply {
                return@forEach
            }

            component.addChild(UIAddress(ownerState, state))
        }

        children.forEach { component.removeChild(it) }
    }
}
