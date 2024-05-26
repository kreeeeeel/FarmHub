package com.project.steamfarm.ui.view.block.accounts

import com.project.steamfarm.Runner
import com.project.steamfarm.model.UserModel
import javafx.animation.FadeTransition
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.util.Duration

val DEFAULT_PHOTO = Image(Runner::class.java.getResource("images/photo.png")!!.toURI().toString())

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

}