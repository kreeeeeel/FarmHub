package com.project.steamfarm.ui.view.block.accounts

import com.project.steamfarm.model.UserModel
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane

abstract class AccountView(
    val content: AnchorPane
) {

    protected var nodes: List<Pane> = emptyList()

    abstract fun view(users: List<UserModel>)
    abstract fun search(prefix: String)

}