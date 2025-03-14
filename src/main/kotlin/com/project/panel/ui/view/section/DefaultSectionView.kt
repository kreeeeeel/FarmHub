package com.project.panel.ui.view.section

import com.project.panel.langApplication
import com.project.panel.ui.controller.BaseController.Companion.root
import com.project.panel.ui.view.SectionType
import com.project.panel.ui.view.menu.user.USER_EDIT_MENU_ID
import com.project.panel.ui.view.menu.user.USER_MENU_VIEW_ID
import javafx.scene.control.Label
import javafx.scene.layout.Pane

const val SECTION_ID = "section"
const val SECTION_NAME_ID = "sectionName"
const val SECTION_CLOSE_ID = "sectionClose"

private const val SECTION_LAYOUT_X = 280.0

abstract class DefaultSectionView(
    private var sectionType: SectionType,
) {

    protected val section = Pane().also {
        it.id = SECTION_ID
        it.layoutX = SECTION_LAYOUT_X
        it.layoutY = 55.0
    }

    private val sectionName = Label().also {
        it.id = SECTION_NAME_ID
        it.layoutX = 310.0
        it.layoutY = 24.0
    }

    private val sectionClose = Label().also {
        it.id = SECTION_CLOSE_ID
        it.layoutX = 310.0
        it.layoutY = 42.0
    }


    abstract fun refreshLanguage()

    open fun initialize() {

        root.children.removeIf {
            it.id == section.id || it.id == sectionName.id || it.id == sectionClose.id || it.id == USER_EDIT_MENU_ID ||
                    it.id == USER_MENU_VIEW_ID
        }
        root.children.add(section)

        refreshLanguage()
        sectionLang()

        if (sectionType != SectionType.START) {
            root.children.addAll(sectionName, sectionClose)
        }
    }

    private fun sectionLang() {
        sectionName.text = when (sectionType) {
            SectionType.USERS -> langApplication.text.accounts.name
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