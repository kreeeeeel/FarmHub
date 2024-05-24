package com.project.steamfarm.ui.view.block.accounts

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import com.project.steamfarm.ui.view.section.CONTENT_HEIGHT
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import kotlin.math.max

private const val USER_ID = "userTable"
private const val DEFAULT_LAYOUT_X = 14.0
private const val DEFAULT_LAYOUT_Y = 14.0

class AccountTableView(content: AnchorPane): AccountView(content) {

    override fun view(users: List<UserModel>) {
        content.children.clear()

        nodes = users.map { getUser(it) }
        setPositionNodes(nodes)
    }

    override fun search(prefix: String) {

        val users = nodes.filter {
            val node = it.children.firstOrNull { c -> c.id == "userAuthName" } ?: return@filter false
            val username = node as Label
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
            l.layoutY = 55.0
        }

        it.children.addAll(icon, auth, name, wait)

    }

}
