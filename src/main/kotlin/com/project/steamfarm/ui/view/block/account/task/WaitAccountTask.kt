package com.project.steamfarm.ui.view.block.account.task

import com.project.steamfarm.data.TimerType
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import com.project.steamfarm.repository.Repository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.view.block.account.AccountView
import com.project.steamfarm.ui.view.block.account.USER_CARD_BLOCK_ID
import com.project.steamfarm.ui.view.block.account.USER_LIST_BLOCK_ID
import com.project.steamfarm.ui.view.section.DefaultSectionView
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import java.util.TimerTask

class WaitAccountTask(
    private var userPane: Pane,
    private var userModel: UserModel,
    private var accountView: AccountView
): TimerTask() {

    private val userRepository: Repository<UserModel> = UserRepository()
    private var counter = 0

    override fun run() {

        val block = userPane.children.firstOrNull { it.id == USER_CARD_BLOCK_ID || it.id == USER_LIST_BLOCK_ID }
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

            if (user.userType != UserType.WAIT_AUTH) updateView(user)
            counter = 0
        }
    }

    private fun getTime(time: Long): String {
        val avg = (System.currentTimeMillis() - time) / 1000
        val minutes = avg / 60
        val seconds = avg % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateView(user: UserModel) {
        DefaultSectionView.finishTask(userModel.username, TimerType.WAIT_AUTH)

        val offsetX = userPane.layoutX
        val offsetY = userPane.layoutY

        val newUserPane = if (user.userType == UserType.AUTH_COMPLETED) {
            accountView.getUserCompleted(user)
        } else accountView.getUserBadAuth(user)

        newUserPane.layoutX = offsetX
        newUserPane.layoutY = offsetY

        val index = accountView.nodes.indexOf(userPane)
        accountView.nodes.add(index, userPane)

        userModel = user
        Platform.runLater {
            accountView.content.children.remove(userPane)
            accountView.content.children.add(newUserPane)
        }
    }

}