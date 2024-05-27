package com.project.steamfarm.ui.view.block.account.view

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import com.project.steamfarm.ui.view.block.account.*
import com.project.steamfarm.ui.view.section.CONTENT_HEIGHT
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import kotlin.math.max

private const val DEFAULT_LAYOUT_X = 14.0
private const val DEFAULT_LAYOUT_Y = 8.0

private const val BUTTON_TABLE_ID = "userTableButton"
private const val BUTTON_TABLE_DELETE_ID = "userTableRemoveButton"

class AccountCardView(
    private val scroll: ScrollPane,
    content: AnchorPane
): AccountView(content) {

    private var prevActiveUser: Pane? = null

    override fun setPositionNodes(nodes: List<Pane>, isAnimate: Boolean) {

        Platform.runLater { content.children.clear() }

        if (nodes.isNotEmpty()) {

            var countVertical = 0
            var countHorizontal = 0

            nodes.forEach {
                it.layoutX = DEFAULT_LAYOUT_X + 250 * countHorizontal++
                it.layoutY = DEFAULT_LAYOUT_Y + 106 * countVertical

                if (countHorizontal == 2) {
                    countHorizontal = 0
                    countVertical++
                }
            }

            Platform.runLater {
                content.children.addAll(nodes)
                content.prefHeight = max(CONTENT_HEIGHT, DEFAULT_LAYOUT_Y + 106 * countVertical)

                disablePrevActiveUser()
                if (isAnimate) animateSequentially(content.children)
            }
        } else Platform.runLater { NotFoundView(content).view() }
    }

    override fun getUserWait(user: UserModel) = Pane().also {
        it.id = USER_CARD_ID

        val block = Pane().also { p ->
            p.id = USER_CARD_BLOCK_ID
            p.layoutX = 5.0
            p.layoutY = 5.0
        }

        val icon = ImageView().also { img ->
            img.id = "time"
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutX = 68.0
            img.layoutY = 6.0
        }

        val time = Label(getTime(user.time)).also { l ->
            l.id = "authTime"
            l.layoutX = 100.0
            l.layoutY = 8.0
        }

        val username = Label(user.username).also { l ->
            l.id = USER_CARD_WAIT_NAME_ID
            l.layoutY = 40.0
        }

        val auth = Label(langApplication.text.accounts.authorization.name).also { l ->
            l.id = "userAuth"
            l.layoutY = 55.0
        }

        block.children.addAll(icon, time, username, auth)
        it.children.addAll(block)

        super.userToAppendTask(user, it)
    }

    override fun getUserCompleted(user: UserModel) = Pane().also {
        it.id = USER_CARD_ID

        val block = Pane().also { p ->
            p.id = USER_CARD_BLOCK_ID
            p.layoutX = 5.0
            p.layoutY = 5.0
        }

        val image = photoRepository.findById(user.username) ?: DEFAULT_PHOTO
        val photo = Circle().also { c ->
            c.fill = ImagePattern(image)
            c.layoutX = 30.0
            c.layoutY = 28.0
            c.radius = 16.0
        }

        val username = Label(user.username).also { l ->
            l.id = USER_CARD_COMPLETED_NAME_ID
            l.layoutX = 50.0
            l.layoutY = 12.0
        }

        val login = Label(langApplication.text.accounts.login).also { l ->
            l.id = "userCompletedLogin"
            l.layoutX = 50.0
            l.layoutY = 28.0
        }

        super.initDota(it, user.gameStat.dotaHour)
        super.initCs(it, user.gameStat.csDropped)

        block.children.addAll(photo, username, login)
        it.children.add(block)

        it.setOnMouseClicked { _ -> setPrevActiveUser(it, user) }
    }

    override fun getUserBadAuth(user: UserModel) = Pane().also {
        it.id = USER_CARD_BAD_ID

        val block = Pane().also { p ->
            p.id = USER_CARD_BLOCK_ID
            p.layoutX = 5.0
            p.layoutY = 5.0
        }

        val icon = ImageView().also { img ->
            img.id = "cross"
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutX = 8.0
            img.layoutY = 6.0
        }

        val username = Label(user.username).also { l ->
            l.id = USER_CARD_BAD_NAME_ID
            l.layoutX = 38.0
            l.layoutY = 8.0
        }

        val hint = Label(langApplication.text.accounts.authorization.badAuth).also { l ->
            l.id = "userBadAuth"
            l.layoutX = 5.0
            l.layoutY = 40.0
        }

        block.children.addAll(icon, username, hint)
        it.children.addAll(block)

        it.setOnMouseClicked { _ -> setPrevActiveUser(it, user) }
    }

    private fun disablePrevActiveUser() {
        prevActiveUser?.let {
            it.id = if (it.id == USER_CARD_BAD_ACTIVE_ID) USER_CARD_BAD_ID else USER_CARD_ID
            it.children.removeIf { n -> n.id == BUTTON_TABLE_ID || n.id == BUTTON_TABLE_DELETE_ID }
        }
        prevActiveUser = null
    }

    private fun setPrevActiveUser(pane: Pane, user: UserModel) {

        val dotaBtn = getButtonActive(user, ButtonType.DOTA)
        val csBtn = getButtonActive(user, ButtonType.CS)
        val deleteBtn = getButtonActive(user, ButtonType.REMOVE, user.userType == UserType.BAD_AUTH)

        prevActiveUser?.let {
            it.id = if (it.id == USER_CARD_BAD_ACTIVE_ID) USER_CARD_BAD_ID else USER_CARD_ID
            it.children.removeIf { n -> n.id == BUTTON_TABLE_ID || n.id == BUTTON_TABLE_DELETE_ID }
        }

        if (prevActiveUser != pane) {
            pane.id = if (user.userType == UserType.BAD_AUTH) USER_CARD_BAD_ACTIVE_ID else USER_CARD_ACTIVE_ID
            pane.children.add(deleteBtn)

            if (user.userType == UserType.AUTH_COMPLETED) {
                pane.children.addAll(dotaBtn, csBtn)
            }

            val paneCenterY = pane.layoutY + pane.height / 2
            val visibleHeight = scroll.viewportBounds.height
            val totalHeight = content.prefHeight

            val newValue = (paneCenterY - visibleHeight / 2) / (totalHeight - visibleHeight)
            scroll.vvalue = newValue.coerceIn(0.0, 1.0)

            pane.toFront()
            prevActiveUser = pane
        } else prevActiveUser = null
    }

    private fun getButtonActive(user: UserModel, type: ButtonType, isBadUser: Boolean = false) = Pane().also {
        it.id = if (type == ButtonType.REMOVE && !isBadUser) BUTTON_TABLE_DELETE_ID else BUTTON_TABLE_ID
        it.layoutX = 5.0
        it.layoutY = when(type) {
            ButtonType.DOTA -> 95.0
            ButtonType.CS -> 135.0
            ButtonType.REMOVE -> if (isBadUser) 95.0 else 175.0
        }

        val icon = ImageView().also { img ->
            img.id = when(type) {
                ButtonType.DOTA -> "dota"
                ButtonType.CS -> "cs"
                ButtonType.REMOVE -> "bag"
            }
            img.layoutX = if (type == ButtonType.DOTA || type == ButtonType.CS) 8.0 else 10.0
            img.layoutY = 6.0
            img.fitWidth = if (type == ButtonType.DOTA || type == ButtonType.CS) 24.0 else 20.0
            img.fitHeight = img.fitHeight
        }

        val text = Label().also { l ->
            l.id = "userTableButtonText"
            l.text = when(type) {
                ButtonType.DOTA -> getUsedText(user.gameStat.farmDota)
                ButtonType.CS -> getUsedText(user.gameStat.farmCs)
                ButtonType.REMOVE -> langApplication.text.accounts.action.remove
            }
            l.layoutX = 32.0
            l.layoutY = 8.0
        }

        it.setOnMouseClicked { _ -> updateUserStat(text, user, type) }

        it.children.addAll(icon, text)
    }

    private fun updateUserStat(text: Label, user: UserModel, type: ButtonType) {
        when(type) {
            ButtonType.DOTA -> {
                user.gameStat.farmDota = !user.gameStat.farmDota
                text.text = getUsedText(user.gameStat.farmDota)
                userRepository.save(user)
            }
            ButtonType.CS -> {
                user.gameStat.farmCs = !user.gameStat.farmCs
                text.text = getUsedText(user.gameStat.farmCs)
                userRepository.save(user)
            }
            ButtonType.REMOVE -> TODO()
        }
    }

    private fun getUsedText(isUsed: Boolean): String {
        return if (isUsed) {
            langApplication.text.accounts.action.unusedFarm
        } else langApplication.text.accounts.action.useFarm
    }

}
