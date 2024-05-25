package com.project.steamfarm.ui.view.block.accounts

import com.project.steamfarm.Runner
import com.project.steamfarm.data.TimerData
import com.project.steamfarm.data.TimerType
import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import com.project.steamfarm.repository.Repository
import com.project.steamfarm.repository.impl.PhotoRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.view.section.CONTENT_HEIGHT
import com.project.steamfarm.ui.view.section.DefaultSectionView
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import java.util.*
import kotlin.math.max

private const val USER_ID = "userTable"
private const val USER_BLOCK_ID = "userTableBlock"
private const val USER_BAD_BLOCK_ID = "userTableBadBlock"
private const val DEFAULT_LAYOUT_X = 14.0
private const val DEFAULT_LAYOUT_Y = 8.0

private val DEFAULT_PHOTO = Runner::class.java.getResourceAsStream("photo.png")

class AccountTableView(content: AnchorPane): AccountView(content) {

    private val photoRepository: Repository<Image> = PhotoRepository()

    override fun view(users: List<UserModel>) {
        content.children.clear()

        nodes = users.map { getUser(it) }.toMutableList()
        setPositionNodes(nodes)
    }

    override fun search(prefix: String) {

        val users = nodes.filter {
            val node = it.children.firstOrNull { n -> n.id == USER_BLOCK_ID }
            val pane = node as Pane

            val field = pane.children.firstOrNull { n ->
                n.id == "userAuthName" || n.id == "userCompletedName" || n.id == "userBadAuthName"
            }

            if (field != null) {
                val username = field as Label
                return@filter username.text.startsWith(prefix)
            }

            return@filter false
        }
        setPositionNodes(users)

    }

    private fun setPositionNodes(nodes: List<Pane>) {

        content.children.clear()

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

            content.children.addAll(nodes)
            content.prefHeight = max(CONTENT_HEIGHT, DEFAULT_LAYOUT_Y + 106 * countVertical)
            animateSequentially(content.children)
        } else {
            val notFoundView = NotFoundView(content)
            notFoundView.view()
        }
    }

    private fun getUser(user: UserModel): Pane = when(user.userType) {
        UserType.WAIT_AUTH -> getUserWait(user)
        UserType.AUTH_COMPLETED -> getUserCompleted(user)
        UserType.BAD_AUTH -> getUserBadAuth(user)
    }

    private fun getUserWait(user: UserModel) = Pane().also {
        it.id = USER_ID

        val block = Pane().also { p ->
            p.id = USER_BLOCK_ID
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
            l.id = "userAuthName"
            l.layoutY = 40.0
        }

        val auth = Label(langApplication.text.accounts.authorization.name).also { l ->
            l.id = "userAuth"
            l.layoutY = 55.0
        }

        block.children.addAll(icon, time, username, auth)
        it.children.addAll(block)

        val timer = Timer()
        timer.schedule(UpdateTime(it, user), 1000, 1000)

        DefaultSectionView.startTask(
            TimerData(
                timer = timer,
                value = user.username,
                type = TimerType.WAIT_AUTH
            )
        )
    }

    private fun getUserCompleted(user: UserModel) = Pane().also {
        it.id = USER_ID

        val block = Pane().also { p ->
            p.id = USER_BLOCK_ID
            p.layoutX = 5.0
            p.layoutY = 5.0
        }

        val image = photoRepository.findById(user.username) ?: Image(DEFAULT_PHOTO)
        val photo = Circle().also { c ->
            c.fill = ImagePattern(image)
            c.layoutX = 46.0
            c.layoutY = 43.0
            c.radius = 32.0
        }

        val username = Label(user.username).also { l ->
            l.id = "userCompletedName"
            l.layoutX = 80.0
            l.layoutY = 12.0
        }

        val login = Label(langApplication.text.accounts.login).also { l ->
            l.id = "userCompletedLogin"
            l.layoutX = 80.0
            l.layoutY = 28.0
        }

        val dota = ImageView().also { img ->
            img.id = "dota"
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutX = 90.0
            img.layoutY = 50.0
        }

        val dotaValueImg = ImageView().also { img ->
            img.id = if (user.gameStat.dotaHour == null) "question" else "done"
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutX = 115.0
            img.layoutY = 50.0
        }

        val hour = String.format("%d%s", user.gameStat.dotaHour ?: 0, langApplication.text.accounts.hour)
        val dotaHourValue = Label(hour).also { l ->
            l.layoutX = 115.0
            l.layoutY = 52.0
        }

        val cs = ImageView().also { img ->
            img.id = "cs"
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutX = 170.0
            img.layoutY = 50.0
        }

        block.children.addAll(photo, username, login, dota, cs)
        if (user.gameStat.dotaHour == null || user.gameStat.dotaHour!! >= 100) {
            block.children.add(dotaValueImg)
        } else block.children.add(dotaHourValue)

        it.children.add(block)
    }

    private fun getUserBadAuth(user: UserModel) = Pane().also {
        it.id = USER_BAD_BLOCK_ID

        val block = Pane().also { p ->
            p.id = USER_BLOCK_ID
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
            l.id = "userBadAuthName"
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
    }

    private fun getTime(time: Long): String {
        val avg = (System.currentTimeMillis() - time) / 1000
        val minutes = avg / 60
        val seconds = avg % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    inner class UpdateTime(
        private var userPane: Pane,
        private var userModel: UserModel
    ): TimerTask() {

        private val userRepository: Repository<UserModel> = UserRepository()
        private var counter = 0

        override fun run() {

            val block = userPane.children.firstOrNull { it.id == USER_BLOCK_ID }
            if (block == null) {
                DefaultSectionView.finishTask(userModel.username, TimerType.WAIT_AUTH)
                return
            }

            val node = (block as Pane).children.firstOrNull { it.id == "authTime" }
            if (node == null) {
                DefaultSectionView.finishTask(userModel.username, TimerType.WAIT_AUTH)
                return
            }

            val time = node as Label
            Platform.runLater { time.text = getTime(userModel.time) }

            if (counter++ >= 5) {

                val user = userRepository.findById(userModel.username)
                if (user == null) {
                    DefaultSectionView.finishTask(userModel.username, TimerType.WAIT_AUTH)
                    return
                }

                if (user.userType != UserType.WAIT_AUTH) {
                    DefaultSectionView.finishTask(userModel.username, TimerType.WAIT_AUTH)

                    val offsetX = userPane.layoutX
                    val offsetY = userPane.layoutY

                    val newUserPane = if (user.userType == UserType.AUTH_COMPLETED) {
                        getUserCompleted(user)
                    } else getUserBadAuth(user)

                    newUserPane.layoutX = offsetX
                    newUserPane.layoutY = offsetY

                    val index = nodes.indexOf(userPane)
                    nodes.add(index, userPane)

                    userModel = user
                    Platform.runLater {
                        content.children.remove(userPane)
                        content.children.add(newUserPane)
                    }
                }
                counter = 0
            }
        }
    }

}
