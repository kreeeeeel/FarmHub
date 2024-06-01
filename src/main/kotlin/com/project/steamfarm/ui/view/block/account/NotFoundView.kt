package com.project.steamfarm.ui.view.block.account

import com.project.steamfarm.langApplication
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane

class NotFoundView(
    private val content: AnchorPane
) {

    private val logo = ImageView().also {
        it.id = "404"
        it.fitWidth = 96.0
        it.fitHeight = 96.0
        it.layoutX = 205.0
        it.layoutY = 90.0
    }

    private val title = Label(langApplication.text.accounts.notFound).also {
        it.id = "notFound"
        it.layoutX = 12.0
        it.layoutY = 210.0
    }

    private val hint = Label(langApplication.text.accounts.hintToImport).also {
        it.id = "notFoundHint"
        it.layoutX = 12.0
        it.layoutY = 230.0
    }

    fun view() {
        content.children.addAll(logo, title, hint)
    }

}