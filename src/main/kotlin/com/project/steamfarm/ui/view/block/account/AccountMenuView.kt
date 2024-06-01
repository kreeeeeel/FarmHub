package com.project.steamfarm.ui.view.block.account

import com.project.steamfarm.Runner
import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.repository.Repository
import com.project.steamfarm.repository.impl.UserRepository
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.shape.Line

const val MENU_ID = "accountMenu"
private const val MENU_BLOCK_ID = "accountBlockMenu"

private val DEFAULT_PHOTO = Image(Runner::class.java.getResource("images/Alchemist_icon.png")!!.toURI().toString())

class AccountMenuView {

    private val userRepository: Repository<UserModel> = UserRepository()

    fun view(user: UserModel, userView: Pane) = Pane().also { pane ->
        pane.id = MENU_ID

        val block = getBlockMenu(user)

        val changeHero = getButton(ButtonType.HERO)
        val line = Line().also { l ->
            l.layoutX = 120.0
            l.layoutY = 185.0
            l.startX = -100.0
            l.endX = 100.0
        }
        val farmingDotaGame = getButton(ButtonType.DOTA, user.gameStat.enableDota)
        val farmingCsGame = getButton(ButtonType.CS, user.gameStat.enableCs)
        val dropUser = getButton(ButtonType.DROP)

        farmingDotaGame.setOnMouseClicked {
            user.gameStat.enableDota = updateUserStat(block, user.gameStat.enableDota, farmingDotaGame, ButtonType.DOTA)
            updateUserView(userView, user)
            userRepository.save(user)
        }
        farmingCsGame.setOnMouseClicked {
            user.gameStat.enableCs = updateUserStat(block, user.gameStat.enableCs, farmingCsGame, ButtonType.CS)
            updateUserView(userView, user)
            userRepository.save(user)
        }
        pane.children.addAll(block, changeHero, line, farmingDotaGame, farmingCsGame, dropUser)
    }

    private fun getBlockMenu(user: UserModel) = Pane().also { pane ->
        pane.id = MENU_BLOCK_ID

        val currentHeroIcon = ImageView().also {
            it.image = DEFAULT_PHOTO
            it.layoutX = 70.0
            it.layoutY = 14.0
            it.fitWidth = 100.0
            it.fitHeight = 50.0
        }

        val currentHeroName = Label().also {
            it.text = "Alchemist"
            it.id = "accountHeroName"
            it.layoutY = 71.0
        }

        val currentDotaStatus = getGameStatus(true, user.gameStat.enableDota)
        val currentCsStatus = getGameStatus(false, user.gameStat.enableCs)
        pane.children.addAll(currentHeroIcon, currentHeroName, currentDotaStatus, currentCsStatus)
    }

    private fun getGameStatus(isDota: Boolean, isEnabled: Boolean) = Pane().also {
        it.id = "accountGameStatus"
        it.layoutX = if (isDota) 35.0 else 135.0
        it.layoutY = 95.0

        val game = ImageView().also { img ->
            img.id = if (isDota) "dota" else "cs"
            img.layoutX = 10.0
            img.layoutY = 3.0
        }

        val status = ImageView().also { img ->
            img.id = if (isEnabled) "on" else "off"
            img.layoutX = 40.0
            img.layoutY = 3.0
        }

        it.children.addAll(game, status)
    }

    private fun findGameStatus(block: Pane, id: String): ImageView? {
        return block.children
            .filter { node -> node.id == "accountGameStatus" }
            .mapNotNull { node ->
                val pane = node as Pane
                pane.children.filterIsInstance<ImageView>()
                    .firstOrNull { it.id == id } ?: return@mapNotNull null

                pane.children.filterIsInstance<ImageView>().firstOrNull { it.id == "on" || it.id == "off" }
            }
            .firstOrNull()
    }

    private fun getButton(type: ButtonType, isEnabled: Boolean = false) = Pane().also {
        it.id = if (type == ButtonType.DROP) "accountRedButton" else "accountButton"
        it.layoutX = 5.0
        it.layoutY = when(type) {
            ButtonType.HERO -> 140.0
            ButtonType.DOTA -> 190.0
            ButtonType.CS -> 235.0
            ButtonType.DROP -> 280.0
        }

        val icon = ImageView().also { img ->
            img.id = when(type) {
                ButtonType.HERO -> "pudge"
                ButtonType.DOTA -> "dota"
                ButtonType.CS -> "cs"
                ButtonType.DROP -> "bag"
            }
            img.layoutX = 14.0
            img.layoutY = 7.0
            img.fitWidth = 24.0
            img.fitHeight = img.fitWidth
        }

        val text = Label().also { l ->
            l.id = "accountButtonText"
            l.layoutX = 48.0
            l.layoutY = 10.0
            l.text = when(type) {
                ButtonType.HERO -> langApplication.text.accounts.action.chooseHero
                ButtonType.DROP -> langApplication.text.accounts.action.dropAccount
                else -> if (isEnabled) {
                    langApplication.text.accounts.action.disableFarmGame
                } else langApplication.text.accounts.action.enableFarmGame
            }
        }

        it.children.addAll(icon, text)
    }

    private fun updateUserStat(
        block: Pane, value: Boolean, currentButton: Pane, buttonType: ButtonType
    ): Boolean {
        val id = if (buttonType == ButtonType.DOTA) "dota" else "cs"

        findGameStatus(block, id)?.let { it.id = if (!value) "on" else "off" }
        currentButton.children.firstOrNull { it.id == "accountButtonText" }?.let {
            val text = it as Label
            text.text = when(!value) {
                true -> langApplication.text.accounts.action.disableFarmGame
                false -> langApplication.text.accounts.action.enableFarmGame
            }
        }


        return !value
    }

    private fun updateUserView(userView: Pane, user: UserModel) {
        /*userView.id = if (user.gameStat.enableCs && user.gameStat.enableDota) USER_ALL_VIEW
        else if (user.gameStat.enableDota) USER_DOTA_VIEW
        else if (user.gameStat.enableCs) USER_CS_VIEW
        else USER_DEFAULT_VIEW*/
    }

    private enum class ButtonType {
        HERO, DOTA, CS, DROP
    }

}