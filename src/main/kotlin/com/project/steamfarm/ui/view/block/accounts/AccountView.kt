package com.project.steamfarm.ui.view.block.accounts

import com.project.steamfarm.model.UserModel
import javafx.scene.layout.AnchorPane

abstract class AccountView(
    val content: AnchorPane
) {

    abstract fun view(users: List<UserModel>)
    abstract fun search(login: String, users: List<UserModel>)

}