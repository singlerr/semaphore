package io.github.singlerr.semaphore.client.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import io.github.singlerr.semaphore.client.gui.widgets.UIResourceImage
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity
import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest

class GuiPhoneScreen :
    WindowScreen(ElementaVersion.V6), EntityPresenter, CallRequestPresenter, CallResponsePresenter {

    init {
        setupBackground()
    }

    private fun setupBackground() {
        UIResourceImage(BACKGROUND_IMAGE).constrain {
            x = 2.pixels()
            y = 2.pixels()

            width = ImageAspectConstraint()
            height = (window.getHeight() - 2).pixels
        } childOf window
    }

    override fun presentError(error: ErrorEntity?) {}

    override fun present(entity: PresentableEntity?) {
        TODO("Not yet implemented")
    }

    override fun present(entities: MutableList<PresentableEntity>?) {
        TODO("Not yet implemented")
    }

    override fun present(request: InverseCallRequest?) {
        TODO("Not yet implemented")
    }

    override fun error(entity: Error?) {
        TODO("Not yet implemented")
    }

    override fun present(entity: CallResponse?) {
        TODO("Not yet implemented")
    }
}
