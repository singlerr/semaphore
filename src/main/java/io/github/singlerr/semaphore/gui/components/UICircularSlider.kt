package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIShape
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain

class UICircularSlider(private val r: Float) : UIContainer() {

    init{
        (UIShape() childOf  this).apply {

        }
    }
}