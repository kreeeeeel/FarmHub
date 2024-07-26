package com.project.panel.ui.view.menu.user

import com.project.panel.langApplication
import com.project.panel.model.UserModel
import com.project.panel.repository.impl.UserRepository
import com.project.panel.ui.controller.BaseController.Companion.root
import com.project.panel.ui.view.section.GAME_CS_ID
import com.project.panel.ui.view.section.GAME_DOTA_ID
import com.project.panel.utils.ModeUtils
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.shape.Line

@Suppress("unused")
class UserEditStatusMenuView: DefaultUserMenuView() {

    init {
        menu.id = USER_EDIT_MENU_ID
        menu.layoutX = 525.0
        menu.layoutY = 120.0

        val line = Line().also { l ->
            l.layoutX = 100.0
            l.layoutY = 80.0
            l.startX = -100.0
            l.endX = 100.0
        }

        menu.children.add(line)
    }

    private val enableGameDota = getButtonMenu(
        iconId = GAME_DOTA_ID,
        value = langApplication.text.accounts.action.enableFarmGame
    ).also {
        it.setOnMouseClicked { updateStatus(isEnableDota = true) }
        menu.children.add(it)
    }

    private val disableGameDota = getButtonMenu(
        iconId = GAME_DOTA_ID,
        value = langApplication.text.accounts.action.disableFarmGame
    ).also {
        it.layoutY = 40.0
        it.setOnMouseClicked { updateStatus(isEnableDota = false) }
        menu.children.add(it)
    }

    private val enableGameCs = getButtonMenu(
        iconId = GAME_CS_ID,
        value = langApplication.text.accounts.action.enableFarmGame
    ).also {
        it.layoutY = 81.0
        it.setOnMouseClicked { updateStatus(isEnableCs = true) }
        menu.children.add(it)
    }

    private val disableGameCs = getButtonMenu(
        iconId = GAME_CS_ID,
        value = langApplication.text.accounts.action.disableFarmGame
    ).also {
        it.layoutY = 121.0
        it.setOnMouseClicked { updateStatus(isEnableCs = false) }
        menu.children.add(it)
    }

    private lateinit var userModels: MutableList<UserModel>
    private lateinit var userStatusLabel: MutableList<Label>

    fun setUserData(userModels: MutableList<UserModel>, userStatusLabel: MutableList<Label>) {
        this.userModels = userModels
        this.userStatusLabel = userStatusLabel
    }

    override fun openMenu(): Pane {
        Platform.runLater {
            root.children.add(menu)
            animateScaleMenu(menu)
        }
        return menu
    }

    private fun updateStatus(isEnableDota: Boolean? = null, isEnableCs: Boolean? = null) {
        var value = 0
        userModels.forEach { u ->
            isEnableDota?.let { u.gameStat.enableDota = it }
            isEnableCs?.let { u.gameStat.enableCs = it }

            Platform.runLater {
                userStatusLabel[value++].text = ModeUtils.getEnabledMode(u.gameStat.enableDota, u.gameStat.enableCs)
            }
            UserRepository.save(u)
        }

    }

}