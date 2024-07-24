package com.project.panel.ui.view

import javafx.animation.TranslateTransition
import javafx.scene.Node
import javafx.util.Duration

interface DefaultView {

    fun initialize()
    fun refreshLanguage()

    fun animation(node: Node, isMovedToX: Boolean = true) {
        val translateTransition = TranslateTransition(Duration.millis(665.0), node)
        if (isMovedToX) {
            translateTransition.fromX = -200.0
            translateTransition.toX = node.layoutX
        } else {
            translateTransition.fromY = -200.0
            translateTransition.toY = node.layoutY
        }
        translateTransition.cycleCount = 1
        translateTransition.isAutoReverse = true
        translateTransition.playFromStart()
    }
}