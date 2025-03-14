package com.project.panel.ui.view.section

import com.project.panel.langApplication
import com.project.panel.ui.controller.BaseController.Companion.root
import com.project.panel.ui.view.SectionType
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

class StartSectionView: DefaultSectionView(SectionType.START) {

    private val robot = ImageView().also {
        it.id = "robot"
        it.layoutX = 212.0
        it.layoutY = 95.0
        it.fitWidth = 96.0
        it.fitHeight = 96.0
    }

    private val welcome = Label(langApplication.text.welcome).also {
        it.id = "welcome"
        it.layoutX = 110.0
        it.layoutY = 240.0
    }

    private val choosePointMenu = Label(langApplication.text.choosePointMenu).also {
        it.id = "choosePoint"
        it.layoutX = 110.0
        it.layoutY = 265.0
    }

    override fun refreshLanguage() {
        welcome.text = langApplication.text.welcome
        choosePointMenu.text = langApplication.text.choosePointMenu
    }

    override fun initialize() {
        section.children.clear()
        section.children.addAll(robot, welcome, choosePointMenu)
        super.initialize()
    }

    fun isStartSection() : Boolean {
        val section = root.children.firstOrNull { it.id == "section" } ?: return false
        (section as Pane).children.firstOrNull { it.id == "robot" } ?: return false
        return true
    }

}