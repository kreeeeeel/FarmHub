package com.project.panel.ui.view.block.settings

import com.project.panel.model.ConfigModel
import javafx.scene.layout.Pane

abstract class SettingsBlockView(
    val id: String
) {

    protected val configModel = ConfigModel().fromFile()

    open val block = Pane().also {
        it.id = "settingsBlock"
        it.layoutX = 21.0
    }

    abstract fun setPrefHeight(): Double
    abstract fun refreshLang()

}