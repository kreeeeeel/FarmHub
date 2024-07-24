package com.project.panel.ui.view.block.settings

import javafx.scene.layout.Pane

abstract class SettingsBlockView(
    val id: String
) {

    val block = Pane().also {
        it.id = "settingsBlock"
        it.layoutX = 21.0
    }

    abstract fun setPrefHeight(): Double
    abstract fun refreshLang()

}