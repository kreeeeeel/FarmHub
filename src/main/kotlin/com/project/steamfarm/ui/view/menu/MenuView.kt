package com.project.steamfarm.ui.view.menu

import com.project.steamfarm.langApplication
import com.project.steamfarm.ui.view.DefaultView
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.controller.NAME_APPLICATION
import com.project.steamfarm.ui.view.section.UserSectionView
import com.project.steamfarm.ui.view.section.DefaultSectionView
import com.project.steamfarm.ui.view.section.SettingsSectionView
import com.project.steamfarm.ui.view.section.StartSectionView
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

private const val USER_ID = "accounts"
private const val FARM_ID = "farm"
private const val SELL_ID = "sell"
private const val SUBSCRIBE_ID = "subscribe"
private const val CLOUD_ID = "cloud"
private const val SETTINGS_ID = "settings"

class MenuView: DefaultView {

    private val menu = Pane().also {
        it.id = "menu"
    }

    private val logo = ImageView().also {
        it.id = "logo"
        it.layoutX = 24.0
        it.layoutY = 14.0
        it.fitWidth = 48.0
        it.fitHeight = 48.0
    }

    private val name = Label(NAME_APPLICATION).also {
        it.id = "name"
        it.layoutX = 81.0
        it.layoutY = 21.0
    }

    private val description = Label(langApplication.text.description).also {
        it.id = "description"
        it.layoutX = 81.0
        it.layoutY = 38.0
    }

    private val basic = Label(langApplication.text.menu.basic).also {
        it.id = "pointNameMenu"
        it.layoutX = 20.0
        it.layoutY = 115.0
    }

    private val users = getPointMenu(USER_ID, langApplication.text.menu.accounts)
    private val farm = getPointMenu(FARM_ID, langApplication.text.menu.farm)
    private val sell = getPointMenu(SELL_ID, langApplication.text.menu.sell)

    private val other = Label(langApplication.text.menu.other).also {
        it.id = "pointNameMenu"
        it.layoutX = 20.0
        it.layoutY = 280.0
    }

    private val subscribe = getPointMenu(SUBSCRIBE_ID, langApplication.text.menu.subscribe)
    private val cloud = getPointMenu(CLOUD_ID, langApplication.text.menu.cloud)
    private val settings = getPointMenu(SETTINGS_ID, langApplication.text.menu.settings)

    private var prevSection: DefaultSectionView? = null

    override fun initialize() {
        menu.children.addAll(
            logo, name, description,
            basic, users, farm, sell,
            other, subscribe, cloud, settings
        )
        root.children.add(menu)

        users.setOnMouseClicked { clickOnMenu(USER_ID, users) }
        settings.setOnMouseClicked { clickOnMenu(SETTINGS_ID, settings) }

        animation(menu)
    }

    override fun refreshLanguage() {

        Platform.runLater {
            description.text = langApplication.text.description
            basic.text = langApplication.text.menu.basic

            refreshPoint(users, langApplication.text.menu.accounts)
            refreshPoint(farm, langApplication.text.menu.farm)
            refreshPoint(sell, langApplication.text.menu.sell)

            other.text = langApplication.text.menu.other

            refreshPoint(subscribe, langApplication.text.menu.subscribe)
            refreshPoint(cloud, langApplication.text.menu.cloud)
            refreshPoint(settings, langApplication.text.menu.settings)
        }

    }

    fun enablePrevActivePoint() {
        val menu = root.children.first { it.id == menu.id } as Pane
        menu.children.firstOrNull { it.id == "pointMenuDisable" }?.let {
            val point = it as Pane

            point.isDisable = false
            point.id = "pointMenu"
        }
    }

    private fun refreshPoint(point: Pane, text: String) {

        val label = point.children.last() as Label
        label.text = text

    }

    private fun getPointMenu(imgId: String, text: String): Pane = Pane().also {
        it.id = "pointMenu"
        it.layoutY = when(imgId) {
            USER_ID -> 135.0
            FARM_ID -> 175.0
            SELL_ID -> 215.0
            SUBSCRIBE_ID -> 300.0
            CLOUD_ID -> 340.0
            else -> 380.0
        }

        val img = ImageView().also { i ->
            i.id = imgId
            i.layoutX = 24.0
            i.layoutY = 8.0
            i.fitWidth = 24.0
            i.fitHeight = 24.0
        }

        val label = Label(text).also { l ->
            l.id = "pointMenuText"
            l.layoutX = 69.0
            l.layoutY = 10.0
        }

        it.children.addAll(img, label)
    }

    private fun clickOnMenu(imgId: String, pane: Pane) {
        val section: DefaultSectionView = when(imgId) {
            SETTINGS_ID -> SettingsSectionView(this)
            USER_ID -> UserSectionView()
            else -> StartSectionView()
        }

        enablePrevActivePoint()

        pane.isDisable = true
        pane.id = "pointMenuDisable"

        prevSection = section

        section.initialize()
    }

}