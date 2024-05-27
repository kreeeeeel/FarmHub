package com.project.steamfarm.ui.view.block.account

import com.project.steamfarm.Runner
import com.project.steamfarm.data.TimerData
import com.project.steamfarm.data.TimerType
import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import com.project.steamfarm.repository.Repository
import com.project.steamfarm.repository.impl.PhotoRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.view.block.account.task.WaitAccountTask
import com.project.steamfarm.ui.view.section.DefaultSectionView.Companion.startTask
import javafx.animation.FadeTransition
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.util.Duration
import java.util.*

const val USER_CARD_ID = "userTable"
const val USER_CARD_ACTIVE_ID = "userTableActive"
const val USER_CARD_BAD_ID = "userTableBadBlock"
const val USER_CARD_BAD_ACTIVE_ID = "userTableBadActive"
const val USER_CARD_BLOCK_ID = "userTableBlock"
const val USER_CARD_WAIT_NAME_ID = "userAuthName"
const val USER_CARD_COMPLETED_NAME_ID = "userCompletedName"
const val USER_CARD_BAD_NAME_ID = "userBadAuthName"

const val USER_LIST_ID = "userList"
const val USER_LIST_BAD_ID = "userListBadBlock"
const val USER_LIST_BLOCK_ID = "userListBlock"
const val USER_LIST_WAIT_NAME_ID = "userAuthName"
const val USER_LIST_COMPLETED_NAME_ID = "userCompletedName"
const val USER_LIST_BAD_NAME_ID = "userBadAuthNameList"

val DEFAULT_PHOTO = Image(Runner::class.java.getResource("images/photo.png")!!.toURI().toString())

abstract class AccountView(
    val content: AnchorPane
) {

    protected val userRepository: Repository<UserModel> = UserRepository()
    protected val photoRepository: Repository<Image> = PhotoRepository()

    var nodes: MutableList<Pane> = mutableListOf()

    abstract fun getUserWait(user: UserModel): Pane
    abstract fun getUserCompleted(user: UserModel): Pane
    abstract fun getUserBadAuth(user: UserModel): Pane
    abstract fun setPositionNodes(nodes: List<Pane>, isAnimate: Boolean = false)

    fun view(users: List<UserModel>, isAnimate: Boolean = true) {
        nodes = users.map { getUser(it).also { p -> p.opacity = 0.0 } }.toMutableList()
        setPositionNodes(nodes, isAnimate)
    }

    fun search(prefix: String) {
        val users = nodes.filter {
            val node = it.children.firstOrNull { n ->
                n.id == USER_CARD_BLOCK_ID || n.id == USER_LIST_BLOCK_ID
            } ?: return@filter false

            val block = node as Pane
            val field = block.children.firstOrNull { n ->
                n.id == USER_CARD_WAIT_NAME_ID || n.id == USER_CARD_COMPLETED_NAME_ID ||
                        n.id == USER_CARD_BAD_NAME_ID || n.id == USER_LIST_WAIT_NAME_ID ||
                        n.id == USER_LIST_BAD_NAME_ID || n.id == USER_LIST_COMPLETED_NAME_ID
            } ?: return@filter false

            val username = field as Label
            return@filter username.text.startsWith(prefix)
        }
        setPositionNodes(users)
    }

    fun getTime(time: Long): String {
        val avg = (System.currentTimeMillis() - time) / 1000
        val minutes = avg / 60
        val seconds = avg % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun animateSequentially(nodes: List<Node>) {
        if (nodes.isEmpty()) return

        val firstNode = nodes.first()
        val remainingNodes = nodes.drop(1)

        val transition = FadeTransition(Duration(50.0), firstNode).also {
            it.fromValue = 0.0
            it.toValue = 1.0
            it.isAutoReverse = true
        }

        transition.setOnFinished {
            firstNode.opacity = 1.0
            animateSequentially(remainingNodes)
        }
        transition.playFromStart()
    }

    fun userToAppendTask(user: UserModel, userPane: Pane) {
        val timer = Timer()
        val waitAccountTask = WaitAccountTask(userPane, user, this)
        timer.schedule(waitAccountTask, 1000, 1000)

        val data = TimerData(timer, user.username, TimerType.WAIT_AUTH)
        startTask(data)
    }

    fun initDota(pane: Pane, value: Int?) = Platform.runLater {

        val icon = ImageView().also { img ->
            img.id = "dota"
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutX = if (pane.id == USER_CARD_BLOCK_ID) 18.0 else 228.0
            img.layoutY = if (pane.id == USER_CARD_BLOCK_ID) 53.0 else 13.0
        }

        val name = Label("Dota 2").also {
            it.id = "dotaName"
            it.layoutX = if (pane.id == USER_CARD_BLOCK_ID) 46.0 else 254.0
            it.layoutY = if (pane.id == USER_CARD_BLOCK_ID) 66.0 else 26.0
        }

        val status = Label().also {
            it.id = if (value == null) "okayValueDota"
            else if (value < 100) "badValueDota"
            else "goodValueDota"

            it.text = if (value == null) "-"
            else "$value ${langApplication.text.hour}"

            it.layoutX = if (pane.id == USER_CARD_BLOCK_ID) 46.0 else 254.0
            it.layoutY = if (pane.id == USER_CARD_BLOCK_ID) 52.0 else 13.0
        }

        if (!pane.children.contains(icon)) pane.children.add(icon)
        if (!pane.children.contains(name)) pane.children.add(name)
        if (!pane.children.contains(status)) pane.children.add(status)
    }

    fun initCs(pane: Pane, value: Boolean?) = Platform.runLater {

        val icon = ImageView().also { img ->
            img.id = "cs"
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutX = if (pane.id == USER_CARD_BLOCK_ID) 115.0 else 320.0
            img.layoutY = if (pane.id == USER_CARD_BLOCK_ID) 54.0 else 13.0
        }

        val name = Label("Counter Strike 2").also {
            it.id = "csName"
            it.layoutX = if (pane.id == USER_CARD_BLOCK_ID) 145.0 else 348.0
            it.layoutY = if (pane.id == USER_CARD_BLOCK_ID) 66.0 else 26.0
        }

        val status = Label().also {
            it.id = when(value) {
                true -> "goodValueCs"
                false -> "badValueCs"
                null -> "okayValueCs"
            }

            it.text = when(value) {
                true -> langApplication.text.accounts.available
                false -> langApplication.text.accounts.available
                null -> langApplication.text.accounts.unknown
            }

            it.layoutX = if (pane.id == USER_CARD_BLOCK_ID) 145.0 else 348.0
            it.layoutY = if (pane.id == USER_CARD_BLOCK_ID) 52.0 else 13.0
        }

        pane.children.addAll(icon, name, status)
    }

    private fun getUser(user: UserModel): Pane = when(user.userType) {
        UserType.WAIT_AUTH -> getUserWait(user)
        UserType.BAD_AUTH -> getUserBadAuth(user)
        UserType.AUTH_COMPLETED -> getUserCompleted(user)
    }

    enum class ButtonType {
        DOTA, CS, REMOVE
    }

}