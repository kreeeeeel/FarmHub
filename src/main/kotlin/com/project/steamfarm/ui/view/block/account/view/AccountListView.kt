package com.project.steamfarm.ui.view.block.account.view

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.ui.view.block.account.*
import com.project.steamfarm.ui.view.section.CONTENT_HEIGHT
import javafx.application.Platform
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import kotlin.math.max

private const val BUTTON_TABLE_ID = "userTableButton"
private const val BUTTON_TABLE_DELETE_ID = "userTableRemoveButton"

private const val DEFAULT_LAYOUT_X = 14.0
private const val DEFAULT_LAYOUT_Y = 10.0

class AccountListView(
    content: AnchorPane
): AccountView(content) {

    private var editMenu: Pane? = null

    override fun setPositionNodes(nodes: List<Pane>, isAnimate: Boolean) {

        Platform.runLater { content.children.clear() }

        if (nodes.isNotEmpty()) {

            var countVertical = 0

            nodes.forEach {
                it.layoutX = DEFAULT_LAYOUT_X
                it.layoutY = DEFAULT_LAYOUT_Y + 66 * countVertical++
            }

            Platform.runLater {
                content.children.addAll(nodes)
                content.prefHeight = max(CONTENT_HEIGHT, DEFAULT_LAYOUT_Y + 66 * countVertical)

                if (isAnimate) animateSequentially(content.children)
            }
        } else Platform.runLater { NotFoundView(content).view() }
    }

    override fun getUserWait(user: UserModel) = Pane().also {
        it.id = USER_LIST_ID

        val block = Pane().also { p ->
            p.id = USER_LIST_BLOCK_ID
            p.layoutX = 5.0
            p.layoutY = 5.0
        }

        val icon = ImageView().also { img ->
            img.id = "time"
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutX = 14.0
            img.layoutY = 14.0
        }

        val time = Label(getTime(user.time)).also { l ->
            l.id = "authTime"
            l.layoutX = 48.0
            l.layoutY = 16.0
        }

        val username = Label(user.username).also { l ->
            l.id = USER_LIST_WAIT_NAME_ID
            l.layoutX = 140.0
            l.layoutY = 10.0
        }

        val login = Label(langApplication.text.accounts.authorization.name).also { l ->
            l.id = "userAuth"
            l.layoutX = 140.0
            l.layoutY = 25.0
        }

        block.children.addAll(icon, time, username, login)
        it.children.addAll(block)

        super.userToAppendTask(user, it)
    }

    override fun getUserCompleted(user: UserModel) = Pane().also {
        it.id = USER_LIST_ID

        val block = Pane().also { p ->
            p.id = USER_LIST_BLOCK_ID
            p.layoutX = 5.0
            p.layoutY = 5.0
        }

        val image = photoRepository.findById(user.username) ?: DEFAULT_PHOTO
        val photo = Circle().also { c ->
            c.fill = ImagePattern(image)
            c.layoutX = 30.0
            c.layoutY = 25.0
            c.radius = 16.0
        }

        val username = Label(user.username).also { l ->
            l.id = USER_LIST_COMPLETED_NAME_ID
            l.layoutX = 60.0
            l.layoutY = 10.0
        }

        val login = Label(langApplication.text.accounts.login).also { l ->
            l.id = "userCompletedLogin"
            l.layoutX = 60.0
            l.layoutY = 25.0
        }

        super.initDota(block, user.gameStat.dotaHour)
        super.initCs(block, user.gameStat.csDropped)

        val edit = Pane().also { p ->
            p.isVisible = false
            p.layoutX = 445.0
            p.layoutY = 13.0
            p.prefWidth = 24.0
            p.prefHeight = 24.0
            p.cursor = Cursor.HAND

            val icon = ImageView().also { img ->
                img.id = "pencil"
                img.fitWidth = 24.0
                img.fitHeight = 24.0
            }
            p.children.add(icon)
        }

        edit.setOnMouseClicked { _ -> showEditMenu(user, it.layoutY + 25)}
        block.children.addAll(photo, username, login, edit)
        it.children.add(block)

        it.setOnMouseEntered { _ -> edit.isVisible = true }
        it.setOnMouseExited { _ -> edit.isVisible = false }
    }

    override fun getUserBadAuth(user: UserModel) = Pane().also {
        it.id = USER_LIST_BAD_ID

        val block = Pane().also { p ->
            p.id = USER_LIST_BLOCK_ID
            p.layoutX = 5.0
            p.layoutY = 5.0
        }

        val username = Label(user.username).also { l ->
            l.id = USER_LIST_BAD_NAME_ID
            l.layoutX = 170.0
            l.layoutY = 7.0
        }

        val hint = Label(langApplication.text.accounts.authorization.badAuth).also { l ->
            l.id = "userBadAuthList"
            l.layoutX = 0.0
            l.layoutY = 26.0
        }

        val bag = Pane().also { p ->
            p.isVisible = false
            p.layoutX = 445.0
            p.layoutY = 13.0
            p.prefWidth = 24.0
            p.prefHeight = 24.0
            p.cursor = Cursor.HAND

            val icon = ImageView().also { img ->
                img.id = "bag"
                img.fitWidth = 24.0
                img.fitHeight = 24.0
            }
            p.children.add(icon)
        }

        it.setOnMouseEntered { bag.isVisible = true }
        it.setOnMouseExited { bag.isVisible = false }

        block.children.addAll(username, hint, bag)
        it.children.addAll(block)
    }

    private fun showEditMenu(user: UserModel, vertical: Double) {

        editMenu?.let { content.children.remove(it) }

        val pane = Pane().also {
            it.id = "editMenu"
            it.layoutX = 255.0
            it.layoutY = vertical
        }

        val dotaUse = getButtonActive(user, ButtonType.DOTA)
        val csUse = getButtonActive(user, ButtonType.CS)
        val delete = getButtonActive(user, ButtonType.REMOVE)

        pane.setOnMouseExited { _ ->
            editMenu?.let { content.children.remove(it) }
            editMenu = null
        }

        pane.children.addAll(dotaUse, csUse, delete)
        content.children.add(pane)

        editMenu = pane
    }

    private fun getButtonActive(user: UserModel, type: ButtonType) = Pane().also {
        it.id = if (type == ButtonType.REMOVE) BUTTON_TABLE_DELETE_ID else BUTTON_TABLE_ID
        it.layoutX = 5.0
        it.layoutY = when(type) {
            ButtonType.DOTA -> 5.0
            ButtonType.CS -> 45.0
            ButtonType.REMOVE -> 85.0
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