package com.project.panel.ui.view.component

import javafx.animation.TranslateTransition
import javafx.scene.layout.Pane
import javafx.util.Duration

private const val ENABLED = "toggleEnable"
private const val DISABLED = "toggleDisable"

class ToggleComponent {

    private val toggle = Pane().apply {
        id = ENABLED
        layoutX = 410.0
        layoutY = 6.0
    }

    private val slider = Pane().apply {
        id = "slider"
        layoutX = 28.0
        layoutY = 2.0

        toggle.children.add(this)
    }

    private var isEnabled = true
    private var startedEnabled: Boolean = false
    private val translateTransition = TranslateTransition(Duration.millis(230.0), slider)

    fun getToggle(isEnabled: Boolean): Pane {
        this.startedEnabled = isEnabled
        this.isEnabled = isEnabled

        toggle.id = if (isEnabled) ENABLED else DISABLED
        slider.layoutX = if (isEnabled) 28.0 else 2.0

        return toggle
    }

    fun action() {
        translateTransition.stop()
        toggle.id = if (isEnabled) DISABLED else ENABLED

        if (startedEnabled) {
            translateTransition.fromX = if (isEnabled) 2.0 else -26.0
            translateTransition.toX = if (isEnabled) -26.0 else 2.0
        } else {
            translateTransition.fromX = if (isEnabled) 26.0 else 2.0
            translateTransition.toX = if (isEnabled) 0.0 else 26.0
        }
        translateTransition.playFromStart()

        isEnabled = !isEnabled
    }

}