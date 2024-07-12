package com.project.steamfarm.ui.view.modal

import com.project.steamfarm.ui.controller.BaseController.Companion.root
import javafx.animation.FadeTransition
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.util.Duration

const val WINDOW_ID = "window"

abstract class DefaultModal {

    protected val window = Pane().also {
        it.id = WINDOW_ID

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

    protected val block: Pane = Pane().also {
        window.children.add(it)
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

    fun animateFadeTransition(nodes: List<Node>, millis: Double) {
        nodes.forEach { it.opacity = 0.0 }

        if (nodes.isEmpty()) return

        val firstNode = nodes.first()
        val remainingNodes = nodes.drop(1)

        val transition = FadeTransition(Duration.millis(millis), firstNode).also {
            it.fromValue = 0.1
            it.toValue = 0.1
        }
        transition.setOnFinished {
            firstNode.opacity = 1.0
            animateFadeTransition(remainingNodes, millis)
        }
        transition.playFromStart()
    }

}