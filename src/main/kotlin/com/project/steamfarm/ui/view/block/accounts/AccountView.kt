package com.project.steamfarm.ui.view.block.accounts

import com.project.steamfarm.model.UserModel
import javafx.animation.FadeTransition
import javafx.scene.Node
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.util.Duration

abstract class AccountView(
    val content: AnchorPane
) {

    protected var nodes: MutableList<Pane> = mutableListOf()

    abstract fun view(users: List<UserModel>)
    abstract fun search(prefix: String)

    fun animateSequentially(nodes: List<Node>) {
        if (nodes.isEmpty()) return

        val firstNode = nodes.first()
        val remainingNodes = nodes.drop(1)

        val transition = FadeTransition(Duration(150.0), firstNode).also {
            it.fromValue = 0.0
            it.toValue = 1.0
            it.isAutoReverse = true
        }

        transition.setOnFinished {
            animateSequentially(remainingNodes)
        }
        transition.playFromStart()
    }

}