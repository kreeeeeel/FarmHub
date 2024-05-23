package com.project.steamfarm.ui.view.section

import com.project.steamfarm.langApplication
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.view.SectionType
import com.project.steamfarm.ui.view.menu.MenuView
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane

private const val SECTION_LAYOUT_X = 280.0

abstract class DefaultSectionView(
    private var sectionType: SectionType,
) {

    val section = Pane().also {
        it.id = "section"
        it.layoutX = SECTION_LAYOUT_X
        it.layoutY = 55.0
    }

    private val sectionName = Label().also {
        it.id = "sectionName"
        it.layoutX = 310.0
        it.layoutY = 24.0
    }

    private val sectionClose = Label().also {
        it.id = "sectionClose"
        it.layoutX = 310.0
        it.layoutY = 42.0
    }

    abstract fun refreshLanguage()

    open fun initialize() {

        refreshLanguage()
        sectionLang()

        root.children.removeIf { it.id == section.id || it.id == sectionName.id || it.id == sectionClose.id }
        root.children.add(section)

        section.children.clear()

        if (sectionType != SectionType.START) {
            root.children.addAll(sectionName, sectionClose)
        }

        root.scene.setOnKeyReleased { event ->
            val menuView = MenuView()
            val startSectionView = StartSectionView()

            if (!startSectionView.isStartSection() && event.code == KeyCode.ESCAPE) {
                root.children.remove(section)
                root.children.removeIf { it.id == "window" }
                if (sectionType != SectionType.START) {
                    root.children.removeAll(sectionName, sectionClose)
                }

                startSectionView.initialize()
                menuView.enablePrevActivePoint()
            }

        }
    }

    private fun sectionLang() {
        sectionName.text = when (sectionType) {
            SectionType.ACCOUNTS -> langApplication.text.accounts.name
            SectionType.FARM -> langApplication.text.farm.name
            SectionType.SELL -> langApplication.text.sell.name
            SectionType.SUBSCRIBE -> langApplication.text.subscribe.name
            SectionType.CLOUD -> langApplication.text.cloud.name
            SectionType.SETTINGS -> langApplication.text.settings.name
            else -> ""
        }
        sectionClose.text = langApplication.text.closeSection
    }

}