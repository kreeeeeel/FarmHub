package com.project.steamfarm.ui.view.modal.import

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.view.modal.DefaultWindow
import com.project.steamfarm.ui.view.section.UserDataView
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView

class DropUserModal(
    private val action: (MutableMap<String, UserDataView>, MutableMap<String, UserDataView>) -> Unit
): DefaultWindow() {

    init {
        block.id = "dropAccount"
        block.layoutX = 220.0
        block.layoutY = 200.0

        val icon = ImageView().also { img ->
            img.id = "alert"
            img.layoutX = 14.0
            img.layoutY = 14.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        val title = Label(langApplication.text.accounts.dropAccount.title).also { l ->
            l.layoutX = 46.0
            l.layoutY = 16.0
        }

        val desc = Label(langApplication.text.accounts.dropAccount.description).also { l ->
            l.id = "dropAccountDesc"
            l.layoutX = 17.0
            l.layoutY = 55.0
        }

        block.children.addAll(icon, title, desc)
    }

    private val delete: Button = Button(langApplication.text.accounts.dropAccount.delete).also {
        it.id = "dropAccountDelete"
        it.layoutX = 14.0
        it.layoutY = 125.0
        it.isFocusTraversable = false

        block.children.add(it)
    }

    private val cancel: Button = Button(langApplication.text.accounts.dropAccount.cancel).also {
        it.id = "dropAccountCancel"
        it.layoutX = 195.0
        it.layoutY = 125.0
        it.isFocusTraversable = false

        block.children.add(it)
    }

    private var currentUsers: MutableMap<String, UserDataView> = HashMap()
    private var selectedUsers: MutableMap<String, UserDataView> = HashMap()

    private var dropUserModel: UserModel? = null

    override fun show() {
        delete.setOnMouseClicked {
            if (dropUserModel == null) dropManyUsers() else dropOneUser()
        }
        cancel.setOnMouseClicked { root.children.remove(window) }
        super.show()
    }

    fun setUsers(
        currentUsers: MutableMap<String, UserDataView>,
        selectedUsers: MutableMap<String, UserDataView>
    ) {
        this.currentUsers = currentUsers
        this.selectedUsers = selectedUsers
    }

    fun setDropUser(userModel: UserModel?) {
        this.dropUserModel = userModel
    }

    private fun dropManyUsers() {
        selectedUsers.forEach { (key, value) ->
            currentUsers.remove(key)
            UserRepository.delete(value.userModel)
        }

        action.invoke(currentUsers, selectedUsers.apply { clear() })
        root.children.remove(window)
    }

    private fun dropOneUser() = dropUserModel?.let {
        currentUsers.remove(it.steam.accountName)
        selectedUsers.remove(it.steam.accountName)

        UserRepository.delete(it)

        action.invoke(currentUsers, selectedUsers)
        root.children.remove(window)
    }

}