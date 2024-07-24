package com.project.steamfarm.ui.view.menu.user

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.HeroModel
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.repository.impl.HeroImageRepository
import com.project.steamfarm.repository.impl.HeroRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.view.modal.DEFAULT_RANDOM_PHOTO
import com.project.steamfarm.ui.view.modal.DropUserModal
import com.project.steamfarm.ui.view.modal.HeroModal
import com.project.steamfarm.ui.view.section.GAME_CS_ID
import com.project.steamfarm.ui.view.section.GAME_DOTA_ID
import com.project.steamfarm.utils.ModeUtils
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.shape.Line
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val USER_MENU_HEROES_ID = "userMenuHeroesView"
private const val USER_MENU_TEXT_ID = "userMenuText"
private const val USER_MENU_HINT_ID = "userMenuTextHint"

private const val USER_MENU_DATE_ID = "date"
private const val USER_MENU_CLOCK_ID = "clock"
private const val USER_MENU_DROP_ID = "money"

const val STATUA_ID = "statua"

private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

class UserInfoMenuView(
    private val dropUserModal: DropUserModal
): DefaultUserMenuView() {

    init {
        menu.id = USER_MENU_VIEW_ID
        val firstLine = Line().also { l ->
            l.layoutX = 100.0
            l.layoutY = 164.0
            l.startX = -100.0
            l.endX = 100.0
        }

        val secondLine = Line().also { l ->
            l.layoutX = 100.0
            l.layoutY = 285.0
            l.startX = -100.0
            l.endX = 100.0
        }

        menu.children.addAll(firstLine, secondLine)
    }

    private var offsetX: Double = 0.0
    private var offsetY: Double = 0.0

    private lateinit var userModel: UserModel
    private lateinit var userStatusLabel: Label

    override fun openMenu(): Pane {
        Platform.runLater {
            initMenu()
            root.children.add(menu)
            animateScaleMenu(menu)
        }
        return menu
    }

    fun setOffsetMenu(offsetX: Double, offsetY: Double) {
        this.offsetX = offsetX
        this.offsetY = offsetY
    }

    fun setUserData(userModel: UserModel, userStatusLabel: Label) {
        this.userModel = userModel
        this.userStatusLabel = userStatusLabel
    }

    private fun initMenu() {

        val userHeroes = getUserHeroes()
        val heroes = Label(langApplication.text.accounts.action.yourHero).also { l ->
            l.id = USER_MENU_HINT_ID
            l.layoutX = 10.0
            l.layoutY = 4.0
        }

        menu.children.removeAll {
            it.id == userHeroes.id || it.id == USER_MENU_DATE_ID || it.id == USER_MENU_CLOCK_ID ||
                    it.id == USER_MENU_DROP_ID || it.id == USER_MENU_TEXT_ID || it.id == USER_MENU_HINT_ID ||
                    it.id == USER_EDIT_BUTTON_MENU_ID
        }
        menu.children.addAll(userHeroes, heroes)

        val formatedDate =  LocalDateTime.ofInstant(
            Instant.ofEpochMilli(userModel.createdTs),
            ZoneId.systemDefault()
        ).format(formatter)

        val playedHoursDota = "${userModel.gameStat.currentPlayedDota} ${langApplication.text.accounts.action.hours}"
        val lastDropDate = if (userModel.gameStat.lastDropCsDate != null)
            userModel.gameStat.lastDropCsDate!!.format(formatter)
        else langApplication.text.accounts.action.unknown

        appendStatsInMenu(USER_MENU_DATE_ID, formatedDate)
        appendStatsInMenu(USER_MENU_CLOCK_ID, playedHoursDota)
        appendStatsInMenu(USER_MENU_DROP_ID, lastDropDate)

        val selectHero = getButtonMenu(STATUA_ID, langApplication.text.accounts.action.chooseHero).also {
            it.layoutY = 165.0

            it.setOnMouseClicked {
                val heroModal = HeroModal()
                heroModal.setUserDate(userModel)
                heroModal.show()
            }
        }

        val statusGameDota = getButtonMenu(GAME_DOTA_ID, getToggledGame(userModel.gameStat.enableDota)).also {
            it.layoutY = 205.0

            switchStatus(it, true)
        }

        val statusGameCs = getButtonMenu(GAME_CS_ID, getToggledGame(userModel.gameStat.enableCs)).also {
            it.layoutY = 245.0

            switchStatus(it, false)
        }

        val deleteUser = getButtonMenu(USER_MENU_DROP_ID, langApplication.text.accounts.action.dropAccount).also {
            it.layoutY = 286.0
            it.setOnMouseClicked { dropUserModal.show() }
        }

        menu.children.addAll(selectHero, statusGameDota, statusGameCs, deleteUser)

        menu.boundsInLocalProperty().addListener { _, _, bounds ->
            menu.layoutX = offsetX - bounds.width + 10.0
            menu.layoutY = if (offsetY + bounds.height > root.scene.window.height) {
                root.scene.window.height - bounds.height - 10.0
            } else offsetY
        }
    }

    private fun getUserHeroes() = Pane().also {
        it.id = USER_MENU_HEROES_ID
        it.layoutY = 20.0

        val heroesImg = userModel.gameStat.priorityHero.mapIndexed { index, value ->

            val heroModel = HeroRepository.findById(value) ?: HeroModel()
            ImageView().also { img ->
                img.layoutX = 12.0 + (30*index)
                img.layoutY = 8.0
                img.fitWidth = 24.0
                img.fitHeight = img.fitWidth
                img.image = HeroImageRepository.findById(heroModel.icon) ?: DEFAULT_RANDOM_PHOTO
            }
        }

        it.children.addAll(heroesImg)
    }

    private fun appendStatsInMenu(id: String, value: String) = Platform.runLater {

        val icon = ImageView().also {
            it.id = id
            it.layoutX = 14.0
            it.layoutY = when(id) {
                USER_MENU_DATE_ID -> 68.0
                USER_MENU_CLOCK_ID -> 102.0
                else -> 134.0
            }
        }

        val text = Label(value).also {
            it.id = USER_MENU_TEXT_ID
            it.layoutX = 45.0
            it.layoutY = icon.layoutY - 2
        }

        val hint = Label().also {
            it.id = USER_MENU_HINT_ID
            it.layoutX = text.layoutX
            it.layoutY = icon.layoutY + 12.0
            it.text = when(id) {
                USER_MENU_DATE_ID -> langApplication.text.accounts.action.createdDate
                USER_MENU_CLOCK_ID -> langApplication.text.accounts.action.clockInGame
                else -> langApplication.text.accounts.action.lastDropDate
            }
        }

        menu.children.addAll(icon, text, hint)
    }

    private fun switchStatus(button: Pane, isGameDota: Boolean) = Platform.runLater {
        button.setOnMouseClicked { _ ->

            if (isGameDota) userModel.gameStat.enableDota = !userModel.gameStat.enableDota
            else userModel.gameStat.enableCs = !userModel.gameStat.enableCs

            val label = button.children.first { n -> n.id == USER_EDIT_MENU_TEXT_ID } as Label
            label.text = getToggledGame(
                if (isGameDota) userModel.gameStat.enableDota else userModel.gameStat.enableCs
            )

            userStatusLabel.text = ModeUtils.getEnabledMode(userModel.gameStat.enableDota, userModel.gameStat.enableCs)
            UserRepository.save(userModel)
        }
    }

    private fun getToggledGame(isEnabled: Boolean): String = if (isEnabled)
        langApplication.text.accounts.action.disableFarmGame
    else langApplication.text.accounts.action.enableFarmGame

}