package com.project.steamfarm.ui.view.window

import com.project.steamfarm.ui.controller.BaseController.Companion.root
import javafx.animation.FadeTransition
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.util.Duration

abstract class DefaultWindow {

    protected val window = Pane().also {
        it.id = "window"

        val close = Pane().also { p ->
            p.id = "footerRight"
            p.layoutX = 730.0
            p.layoutY = 34.0

            val icon = ImageView().also { img ->
                img.id = "close"
                img.fitWidth = 32.0
                img.fitHeight = 32.0
            }

            p.children.add(icon)
            p.setOnMouseClicked { _ -> root.children.remove(it) }
        }

        it.children.add(close)
    }

    open fun show(animate: Boolean) {
        root.children.add(window)

        if (animate) {
            val transition = FadeTransition(Duration(230.0), window).also {
                it.fromValue = 0.0
                it.byValue = 1.0
                it.isAutoReverse = true
            }

            transition.playFromStart()
        }
    }

    open fun show() {
        show(true)
    }

}