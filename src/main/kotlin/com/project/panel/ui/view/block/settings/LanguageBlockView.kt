package com.project.panel.ui.view.block.settings

import com.project.panel.AppRun
import com.project.panel.langApplication
import com.project.panel.model.LangModel
import com.project.panel.repository.impl.LangRepository
import com.project.panel.repository.impl.PATH_LANGUAGES
import com.project.panel.service.logger.LoggerService
import com.project.panel.ui.controller.BaseController.Companion.root
import com.project.panel.ui.view.menu.MenuView
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import java.io.File

private const val PREF_HEIGHT = 45.0

private const val MAX_COUNTS = 12
private const val HEIGHT = 35.0

private val FLAG_URL = AppRun::class.java.getResource("images/flag.png")
    ?: throw NullPointerException("Image 'flag' not found")

const val LANGUAGE_ID = "language"

class LanguageBlockView(
    private val menu: MenuView,
): SettingsBlockView(LANGUAGE_ID) {

    private val languages = LangRepository.findAll()

    lateinit var blockView: List<SettingsBlockView>

    private val langApp = Label(langApplication.text.settings.langApp).also {
        it.layoutX = 14.0
        it.layoutY = 13.0

        block.children.add(it)
    }

    private val currentLang = getPointLanguage(null, langApplication).also {
        it.isDisable = languages.isEmpty()
        it.layoutX = 265.0
        it.layoutY = 5.0

        block.children.add(it)

        it.setOnMouseClicked { _ -> viewAllLanguage() }
    }

    override fun setPrefHeight(): Double {
        block.prefHeight = PREF_HEIGHT

        return PREF_HEIGHT
    }

    override fun refreshLang() {
        langApp.text = langApplication.text.settings.langApp
    }

    private fun viewAllLanguage() {

        val scroll = ScrollPane().also {
            it.id = LANGUAGE_ID
            it.layoutX = 566.0
            it.layoutY = 74.0
            it.prefWidth = 202.0

            it.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            it.vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER

            it.toFront()
        }

        val content = AnchorPane().also {
            it.id = "languages"
            it.prefWidth = 202.0
        }

        var count = 0
        val points = languages.map {
            getPointLanguage(it.code, it).also { p -> p.layoutY = count++ * 35.0 }
        }

        content.children.addAll(points)
        content.prefHeight = count * 35.0

        scroll.content = content

        if (count > MAX_COUNTS) {
            scroll.prefHeight = MAX_COUNTS * HEIGHT
        }

        scroll.setOnMouseExited { root.children.remove(scroll) }

        root.children.add(scroll)
        root.scene.addEventFilter(MouseEvent.MOUSE_CLICKED) { event ->
            if (!scroll.boundsInParent.contains(event.x, event.y)) {
                root.children.remove(scroll)
            }
        }
    }

    private fun updateLang() {
        Platform.runLater {
            val path = String.format("%s\\%s", PATH_LANGUAGES, langApplication.file)
            val file = File(path)

            val flag = currentLang.children.first { it.id == "flag" } as ImageView
            flag.image = if (file.exists()) Image(file.toURI().toString()) else Image(FLAG_URL.toString())

            val name = currentLang.children.first { it.id == "currentLangText" } as Label
            name.text = langApplication.name

            val sectionName = root.children.first { it.id == "sectionName" } as Label
            sectionName.text = langApplication.text.settings.name

            val sectionClose = root.children.first { it.id == "sectionClose" } as Label
            sectionClose.text = langApplication.text.closeSection

            menu.refreshLanguage()
            blockView.forEach { it.refreshLang() }
        }
    }

    private fun getPointLanguage(code: String?, langModel: LangModel) = Pane().also {
        it.id = if (code == null) "currentLang"
        else "currentLangSwitch"

        var image = Image(FLAG_URL.toString())

        langModel.file?.let { code ->
            val path = String.format("%s\\%s", PATH_LANGUAGES, code)
            val file = File(path)

            if (file.exists()) {
                image = Image(file.toURI().toString())
            }
        }

        val icon = ImageView(image).also { img ->
            img.id = "flag"
            img.layoutX = 8.0
            img.layoutY = 6.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        val text = Label(langModel.name).also { l ->
            l.id = "currentLangText"
            l.layoutX = 32.0
            l.layoutY = 7.0
        }

        code?.let { c ->
            it.setOnMouseClicked { _ ->
                langApplication = LangRepository.findById(c) ?: LangModel()
                configModel.let { config ->
                    config.langApp = c
                    config.save()
                }
                LoggerService.getLogger().info("Changing language app to ${langModel.name}")
                updateLang()
            }
        }
        it.children.addAll(icon, text)
    }

}