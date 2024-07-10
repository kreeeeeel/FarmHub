package com.project.steamfarm.ui.view.window.import

import com.project.steamfarm.langApplication
import com.project.steamfarm.ui.view.window.DefaultWindow
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

class DropAccountWindow: DefaultWindow() {

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

        block.children.add(it)
    }

    private val cancel: Button = Button(langApplication.text.accounts.dropAccount.cancel).also {
        it.id = "dropAccountCancel"
        it.layoutX = 190.0
        it.layoutY = 125.0

        block.children.add(it)
    }

    override fun show() {
        window.children.addAll(block)
        super.show()
    }

}