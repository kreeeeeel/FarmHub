package com.project.steamfarm.ui.view.section

import com.project.steamfarm.langApplication
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.view.SectionType
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import java.util.*

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

    protected val timer = Timer()

    fun cancelTimer() = timer.cancel()

    abstract fun refreshLanguage()

    open fun initialize() {

        root.children.removeIf {
            it.id == section.id || it.id == sectionName.id || it.id == sectionClose.id
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