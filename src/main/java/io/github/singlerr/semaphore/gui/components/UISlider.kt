/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.state.percent
import java.awt.Color
import kotlin.math.abs

class UISlider(
    parent: UIComponent,
    private val initialWidth: RelativeConstraint,
    private val min: Float,
    private val max: Float,
    private val defaultValue: Float,
    private val barColor: Color,
    private val valueConsumer: (Float,Float) -> Unit
) : UIBlock() {
    var value = defaultValue

    private val scope = max - min

    private val wState = BasicState<Number>( defaultValue / scope)

    private var buff: Float = defaultValue

    private var enabled = false

    private var flag = false

    private var lastMouseX: Float = 0f

    init {

        val bar = UIBlock(barColor).constrain {
            x = 0.pixels()
            y = CenterConstraint()
            width = 100.percent()
            height = 10.percent()
        } childOf this

        val handle = UIBlock(Color.black).constrain {
            x = wState.percent() boundTo bar
            y = CenterConstraint()
            width = 10.pixels()
            height = 100.percent()
        } childOf this

//        handle.onMouseDrag { mouseX, mouseY, mouseButton ->
//            var diff = mouseX - lastMouseX
//            diff *= 1.2f
//            buff += diff.toFloat()
//            value = kotlin.math.min(kotlin.math.max(buff, min), max)
//
//            val v = value / scope
//
//            wState.set(v)
//            valueConsumer.invoke(v, value)
//            lastMouseX = mouseX
//        }

        parent.onMouseEnter {
            enabled = true
            flag = false
        }

        parent.onMouseLeave {
            enabled = false
            flag = true
        }

        parent.onMouseDrag { mouseX, mouseY, mouseButton ->
            if(! enabled)
                return@onMouseDrag

            if(! flag){
                lastMouseX = mouseX
                flag = true

            }

            var diff = mouseX - lastMouseX
            diff *= 1.2f
            buff += diff
            value = kotlin.math.min(kotlin.math.max(buff, min), max)

            val v = value / scope

            wState.set(v)
            valueConsumer.invoke(v, value)
            lastMouseX = mouseX
        }
//        parent.onMouseEnter {
//            mouseEntered = true
//        }
//
//        parent.onMouseLeave {
//            mouseEntered = false
//        }
//        parent.onMouseRelease {
//            mouseDragging = false
//        }
//        parent.onMouseDrag { mouseX, mouseY, mouseButton ->
//            if(mouseEntered && ! mouseDragging){
//                lastMouseX = mouseX
//                mouseDragging = true
//            }
//
//            if (! mouseDragging) {
//                return@onMouseDrag
//            }
//
//            val xDiff = mouseX - lastMouseX
//
//            var changedValue = widthValue + xDiff * 1.2f
//
//            changedValue = kotlin.math.max(min, changedValue)
//            changedValue = kotlin.math.min(max, changedValue)
//            widthValue = changedValue
//            value = changedValue
//            wState.set((widthValue / scope))
//            lastMouseX = mouseX
//        }
    }
}
