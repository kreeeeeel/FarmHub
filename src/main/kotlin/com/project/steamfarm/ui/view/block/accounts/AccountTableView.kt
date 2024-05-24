package com.project.steamfarm.ui.view.block.accounts

import com.project.steamfarm.data.TimerData
import com.project.steamfarm.data.TimerType
import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import com.project.steamfarm.repository.Repository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.view.section.CONTENT_HEIGHT
import com.project.steamfarm.ui.view.section.DefaultSectionView
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import java.util.Timer
import java.util.TimerTask
import kotlin.math.max

private const val USER_ID = "userTable"
private const val DEFAULT_LAYOUT_X = 14.0
private const val DEFAULT_LAYOUT_Y = 8.0

class AccountTableView(content: AnchorPane): AccountView(content) {

    override fun view(users: List<UserModel>) {
        content.children.clear()

        nodes = users.map { getUser(it) }
        setPositionNodes(nodes)
    }

    override fun search(prefix: String) {

        val users = nodes.filter {
            val node = it.children.firstOrNull { n -> n.id == "userAuthTable" }
            val text = (node as Pane).children.firstOrNull { n -> n.id == "userAuthName" }
            val username = text as Label

            return@filter username.text.startsWith(prefix)
        }
        setPositionNodes(users)

    }

    private fun setPositionNodes(nodes: List<Pane>) {

        content.children.clear()

        var countVertical = 0
        var countHorizontal = 0

        nodes.forEach {
            it.layoutX = DEFAULT_LAYOUT_X + 250*countHorizontal++
            it.layoutY = DEFAULT_LAYOUT_Y + 106*countVertical

            if (countHorizontal == 2) {
                countHorizontal = 0
                countVertical++
            }
        }

        content.children.addAll(nodes)
        content.prefHeight = max(CONTENT_HEIGHT, DEFAULT_LAYOUT_Y + 106*countVertical)
    }

    private fun getUser(user: UserModel): Pane = when(user.userType) {
        UserType.WAIT_AUTH -> getUserWait(user)
        else -> getUserWait(user)
    }

    private fun getUserWait(user: UserModel) = Pane().also {
        it.id = USER_ID

        val block = Pane().also { p ->
            p.id = "userAuthTable"
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

    private fun getTime(time: Long): String {
        val avg = (System.currentTimeMillis() - time) / 1000
        val minutes = avg / 60
        val seconds = avg % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    inner class UpdateTime(
        private val userPane: Pane,
        private val userModel: UserModel
    ): TimerTask() {

        private val userRepository: Repository<UserModel> = UserRepository()
        private var counter = 0

        override fun run() {

            val block = userPane.children.firstOrNull { it.id == "userAuthTable" }
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
                }

                counter = 0
            }
        }
    }

}
