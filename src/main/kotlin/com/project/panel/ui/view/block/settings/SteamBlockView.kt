package com.project.panel.ui.view.block.settings

import com.project.panel.langApplication
import com.project.panel.model.ConfigModel
import com.project.panel.service.logger.LoggerService
import com.project.panel.ui.controller.BaseController.Companion.root
import com.project.panel.ui.view.notify.NotifyView
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File

const val STEAM_ID = "steam"
private const val PREF_HEIGHT = 50.0

class SteamBlockView: SettingsBlockView(STEAM_ID) {

    private val icon = ImageView().apply {
        id = "steam"
        layoutX = 10.0
        layoutY = 9.0
        fitWidth = 32.0
        fitHeight = fitWidth
    }

    private val title = Label(langApplication.text.settings.pathSteam).apply {
        layoutX = 52.0
        layoutY = 9.0
    }

    private val description = Label(langApplication.text.settings.steamNotExist).apply {
        id = "steamPath"
        layoutX = 52.0
        layoutY = 27.0
    }

    private val specify = Button(langApplication.text.settings.specify).apply {
        id = "steamSpecify"
        layoutX = 310.0
        layoutY = 9.0

        setOnMouseClicked { fileChooser() }
    }

    private val configModel = ConfigModel().fromFile()

    override fun setPrefHeight(): Double {
        configModel.steamExecutor?.let { description.text = it }
        block.children.addAll(icon, title, description, specify)
        block.prefHeight = PREF_HEIGHT
        return PREF_HEIGHT
    }

    override fun refreshLang() {
        if (configModel.steamExecutor == null) {
            description.text = langApplication.text.settings.steamNotExist
        }
        title.text = langApplication.text.settings.pathSteam
        specify.text = langApplication.text.settings.specify
    }

    private fun fileChooser(): Unit? = FileChooser().also {
        it.title = langApplication.text.settings.specifySteamExe
        it.extensionFilters.add(FileChooser.ExtensionFilter("Steam exe", "*.exe"))

        configModel.lastDirectoryChooser?.let { dir ->
            val file = File(dir)
            if (file.exists()) it.initialDirectory = File(dir)
        }
    }.showOpenDialog(root.scene.window as Stage)?.let { handler(it) }

    private fun handler(file: File) {
        configModel.lastDirectoryChooser = file.parentFile.absolutePath
        configModel.save()

        if (file.absolutePath.endsWith("steam.exe")) {
            LoggerService.getLogger().info("Changing steam path to ${file.absolutePath}")
            configModel.steamExecutor = "\"${file.absolutePath}\""
            configModel.save()

            description.text = configModel.steamExecutor
            NotifyView.success(langApplication.text.success.pathSteam)
        } else {
            LoggerService.getLogger().error("Bad changing path steam to ${file.absolutePath}")
            NotifyView.failure(langApplication.text.failure.pathSteam)
        }
    }

}