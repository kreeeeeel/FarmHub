package com.project.steamfarm.ui.view.block.accounts

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane

private const val USER_ID = "userTable"

class AccountTableView(content: AnchorPane): AccountView(content) {

    override fun view(users: List<UserModel>) {
        TODO("Not yet implemented")
    }

    override fun search(login: String, users: List<UserModel>) {
        TODO("Not yet implemented")
    }

    private fun getUser(user: UserModel): Pane = when(user.userType) {
        UserType.WAIT_AUTH -> getUserWait(user)
        else -> getUserWait(user)
    }

    private fun getUserWait(user: UserModel) = Pane().also {
        it.id = USER_ID

        val icon = ImageView().also { img ->
            img.id = "time"
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutX = 8.0
            img.layoutY = 6.0
        }

        val auth = Label(langApplication.text.accounts.authorization.name).also { l ->
            l.id = "userAuth"
            l.layoutX = 41.0
            l.layoutY = 8.0
        }

        val name = Label(user.username).also { l ->
            l.id = "userAuthName"
            l.layoutY = 37.0
        }

        val wait = Label(langApplication.text.accounts.authorization.wait).also { l ->
            l.id = "userAuthWait"
            l.layoutY = 37.0
        }

        it.children.addAll(icon, auth, name, wait)

    }
}
