package com.project.steamfarm.ui.view.window.import

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.view.section.USER_NAME_ID
import com.project.steamfarm.ui.view.window.DefaultWindow
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

class DropAccountWindow(
    private val userModels: MutableList<UserModel>,
    private val userNodes: MutableList<Pane>,
    private val userMap: MutableMap<String, Pane>,
    private val action: (MutableList<Pane>, Boolean) -> Unit
): DefaultWindow() {

    private val block = Pane().also {
        it.id = "dropAccount"
        it.layoutX = 220.0
        it.layoutY = 200.0

        val icon = ImageView().also { img ->
            img.id = "alert"
            img.layoutX = 14.0
            img.layoutY = 14.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        val title = Label(langApplication.text.accounts.dropAccount.title).also { l ->
            l.layoutX = 46.0
            l.layoutY = 16.0
        }

        val desc = Label(langApplication.text.accounts.dropAccount.description).also { l ->
            l.id = "dropAccountDesc"
            l.layoutX = 17.0
            l.layoutY = 55.0
        }

        it.children.addAll(icon, title, desc)
    }

    private val delete: Button = Button(langApplication.text.accounts.dropAccount.delete).also {
        it.id = "dropAccountDelete"
        it.layoutX = 14.0
        it.layoutY = 125.0
        it.isFocusTraversable = false

        block.children.add(it)
    }

    private val cancel: Button = Button(langApplication.text.accounts.dropAccount.cancel).also {
        it.id = "dropAccountCancel"
        it.layoutX = 195.0
        it.layoutY = 125.0
        it.isFocusTraversable = false

        block.children.add(it)
    }


    override fun show() {
        window.children.addAll(block)

        delete.setOnMouseClicked {
            dropLogic()
        }
        cancel.setOnMouseClicked { root.children.remove(window) }
        super.show()
    }

    private fun dropLogic() = Platform.runLater {

        val usersDropped = userModels.filter { userMap[it.username] != null }
        userNodes.removeAll {
            val node = it.children.firstOrNull { n -> n.id == USER_NAME_ID } as? Label ?: return@removeAll false
            userMap[node.text] != null
        }

        usersDropped.forEach {
            UserRepository.delete(it)
            userMap.remove(it.username)
            userModels.remove(it)
        }

        action.invoke(userNodes, false)
        root.children.remove(window)

    }

}