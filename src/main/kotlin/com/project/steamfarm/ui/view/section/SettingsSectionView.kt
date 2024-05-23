package com.project.steamfarm.ui.view.section

import com.project.steamfarm.ui.view.SectionType
import com.project.steamfarm.ui.view.block.settings.LANGUAGE_ID
import com.project.steamfarm.ui.view.block.settings.LanguageBlockView
import com.project.steamfarm.ui.view.menu.MenuView
import javafx.scene.control.ScrollPane
import javafx.scene.layout.AnchorPane

class SettingsSectionView(menu: MenuView): DefaultSectionView(SectionType.SETTINGS) {

    private val scroll = ScrollPane().also {
        it.prefWidth = 520.0
        it.prefHeight = 545.0
    }

    private val content = AnchorPane().also {
        it.prefWidth = 500.0
        it.prefHeight = 540.0
    }

    private val settingsBlock = listOf(
        LanguageBlockView(menu)
    )

    override fun refreshLanguage() {
        settingsBlock.forEach { it.refreshLang() }
    }

    override fun initialize() {
        super.initialize()

        content.children.clear()
        var value = 14.0
        settingsBlock.forEach {

            it.block.layoutY = value

            value += it.setPrefHeight() + 15.0
            content.children.add(it.block)
        }

        scroll.content = content
        section.children.add(scroll)

        settingsBlock.first { it.id == LANGUAGE_ID }.blockView = settingsBlock
    }

}