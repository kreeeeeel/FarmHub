package com.project.panel.ui.view.section

import com.project.panel.Runner
import com.project.panel.langApplication
import com.project.panel.model.UserModel
import com.project.panel.repository.impl.CacheRepository
import com.project.panel.repository.impl.UserRepository
import com.project.panel.ui.view.SectionType
import com.project.panel.ui.view.block.user.NotFoundView
import com.project.panel.ui.view.menu.user.DefaultUserMenuView
import com.project.panel.ui.view.menu.user.UserEditStatusMenuView
import com.project.panel.ui.view.menu.user.UserInfoMenuView
import com.project.panel.ui.view.modal.DropUserModal
import com.project.panel.ui.view.modal.import.MaFileModal
import com.project.panel.utils.ModeUtils
import javafx.animation.ScaleTransition
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.util.Duration
import java.util.*
import java.util.concurrent.Executors


private const val USER_VIEW_ID = "userView"
private const val USER_CHECKBOX_EMPTY = "checkBox"
private const val USER_CHECKBOX_SELECTED = "selectedCheckBox"
const val USER_NAME_ID = "userViewName"
private const val USER_MODE_ID = "userViewMode"
private const val USER_GAME_STATUS_ID = "userViewGameStatus"
const val GAME_DOTA_ID = "dota"
const val GAME_CS_ID = "cs"
private const val USER_VIEW_EDIT_ID = "userViewEdit"
private const val SELECT_ALL_ID = "selectAll"
private const val REMOVE_ALL_ID = "removeAll"
private const val EDIT_ID = "pencil"
private const val TRASH_ID = "bag"

const val DOTA_NAME = "Dota 2"
const val CS_NAME = "Counter-Strike 2"

private const val USER_VIEW_Y = 60.0

private val DEFAULT_STEAM_PHOTO = Image(
    Runner::class.java.getResource("images/photo.png")!!
        .toURI()
        .toString()
)

class UserSectionView: DefaultSectionView(SectionType.USERS) {

    private val block = Pane().also {
        it.id = "accountSearch"
        it.layoutX = 21.0
        it.layoutY = 14.0

        val icon = ImageView().also { img ->
            img.id = "search"
            img.layoutX = 14.0
            img.layoutY = 8.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        it.children.add(icon)
    }

    private val search = TextField().also {
        it.id = "loginAccountSearch"
        it.isFocusTraversable = false
        it.promptText = langApplication.text.accounts.search
        it.layoutX = 41.0
        it.layoutY = 4.0

        block.children.add(it)
    }

    private val import = Pane().also {
        it.id = "import"
        it.layoutX = 305.0

        val icon = ImageView().also { img ->
            img.id = "plus"
            img.layoutX = 14.0
            img.layoutY = 8.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        val text = Label(langApplication.text.accounts.import).also { l ->
            l.id = "importText"
            l.layoutX = 42.0
            l.layoutY = 10.0
        }

        it.children.addAll(icon, text)
        block.children.add(it)
    }

    private val selected = Label().also {
        it.text = "${langApplication.text.accounts.selected} 0"
        it.layoutX = 26.0
        it.layoutY = 65.0
    }

    private val count = Label().also {
        it.id = USER_MODE_ID
        it.layoutX = 26.0
        it.layoutY = 84.0
    }

    private val selectAll = viewEdit(SELECT_ALL_ID).also {
        it.setOnMouseClicked { _ ->
            if (it.children[0].id == SELECT_ALL_ID) selectAllUser() else removeAllUser()
        }
    }

    private val edit = viewEdit(EDIT_ID)
    private val trash = viewEdit(TRASH_ID)

    private val content = AnchorPane().also { ap ->
        ap.prefWidth = 505.0
        ap.prefHeight = 424.0

        val icon = ImageView().also {
            it.id = "file"
            it.fitWidth = 96.0
            it.fitHeight = 96.0
            it.layoutX = 205.0
            it.layoutY = 90.0
        }

        val text = Label(langApplication.text.readData).also {
            it.id = "readData"
            it.layoutX = 12.0
            it.layoutY = 210.0
        }

        ap.children.addAll(icon, text)
    }

    private val scroll = ScrollPane().also {
        it.id = "accounts"
        it.layoutY = 100.0
        it.prefWidth = 520.0
        it.prefHeight = 425.0
        it.content = content

        it.vvalueProperty().addListener { _, _, _ -> prevUserMenu?.closeMenu() }
        it.hvalueProperty().addListener { _, _, _ -> prevUserMenu?.closeMenu() }
    }

    private val notFoundView = NotFoundView(content)

    /* Variables for modal window */
    private val dropUserModal = DropUserModal(this::handleUpdatedUsers)

    /* Variables for action with accounts, used in select, edit, menu */
    private var currentUsers: MutableMap<String, UserDataView> = HashMap()
    private var selectedUsers: MutableMap<String, UserDataView> = HashMap()

    /* Variables for action menu */
    private val userEditStatusMenuView = UserEditStatusMenuView()
    private val userInfoMenuView = UserInfoMenuView(dropUserModal)
    private var prevUserMenu: DefaultUserMenuView? = null

    override fun refreshLanguage() {
        search.promptText = langApplication.text.accounts.search
        val importText = import.children.firstOrNull { it.id == "importText" } as? Label
        importText?.text = langApplication.text.accounts.import
    }

    override fun initialize() {

        section.children.addAll(block, scroll, selected, count, selectAll, edit, trash)

        search.textProperty().addListener { _, _, newValue -> search(newValue)}

        import.setOnMouseClicked { _ -> MaFileModal(this::appendUser).show() }
        edit.setOnMouseClicked {
            userEditStatusMenuView.setUserData(
                selectedUsers.map { it.value.userModel }.toMutableList(),
                selectedUsers.map { it.value.status }.toMutableList(),
            )

            prevUserMenu?.closeMenu()
            prevUserMenu = userEditStatusMenuView.apply { openMenu() }
        }
        trash.setOnMouseClicked {
            dropUserModal.setUsers(currentUsers, selectedUsers)
            dropUserModal.setDropUser(null)
            dropUserModal.show()
        }

        val executor = Executors.newCachedThreadPool()
        executor.submit {
            currentUsers = UserRepository.findAll().associate {
                val userView = viewUser(it)
                val userDataView = UserDataView(
                    it, userView,
                    userView.children.first { n -> n.id == USER_CHECKBOX_EMPTY } as ImageView,
                    userView.children.first { n -> n.id == USER_MODE_ID } as Label
                )

                it.steam.accountName to userDataView
            }.toMutableMap()

            viewUsers(currentUsers.values.map { it.userView }, true)
        }
        executor.shutdown()
        super.initialize()
    }

    private fun handleUpdatedUsers(
        currentUsers: MutableMap<String, UserDataView>,
        selectedUsers: MutableMap<String, UserDataView>?
    ) {
        this.currentUsers = currentUsers
        if (selectedUsers != null) {
            this.selectedUsers = selectedUsers
        }

        viewUsers(currentUsers.values.map { it.userView })
    }

    private fun search(prefix: String) = viewUsers(
        currentUsers.filter { it.key.contains(prefix) }
            .map { it.value.userView }
    )

    private fun viewUsers(users: List<Pane>, isAnimate: Boolean = false) = Platform.runLater {
        var vertical = 0
        content.children.clear()
        if (users.isNotEmpty()) {
            users.forEach {
                if (!isAnimate) {
                    it.scaleX = 1.0
                    it.scaleY = 1.0
                }
                it.layoutY = USER_VIEW_Y * vertical++
            }
            content.children.addAll(users)
            if (isAnimate) animateSequentially(users)
        } else notFoundView.view()
        changeIdSelect()
    }

    private fun appendUser(userModel: UserModel) = Platform.runLater {

        val userView = viewUser(userModel)
        val userDataView = UserDataView(
            userModel,
            userView,
            userView.children.first { n -> n.id == USER_CHECKBOX_EMPTY } as ImageView,
            userView.children.first { n -> n.id == USER_MODE_ID } as Label
        )

        currentUsers[userModel.steam.accountName] = userDataView
        if (search.text.isEmpty() || (search.text.isNotEmpty() && userModel.steam.accountName.contains(search.text))) {
            content.children.removeIf {
                it.id == notFoundView.logo.id || it.id == notFoundView.title.id || it.id == notFoundView.hint.id
            }

            userView.layoutY = USER_VIEW_Y * content.children.size
            content.children.add(userView)

            animateSequentially(listOf(userView))
        }
        changeIdSelect()
    }

    private fun viewUser(userModel: UserModel) = Pane().also { pane ->

        pane.id = USER_VIEW_ID
        pane.layoutX = 25.0
        pane.scaleX = 0.0
        pane.scaleY = 0.0

        val select = ImageView().also {
            it.id = USER_CHECKBOX_EMPTY
            it.layoutX = 14.0
            it.layoutY = 18.0
        }

        val photo = ImageView().also {
            it.image = CacheRepository.findById(userModel.steam.accountName) ?: DEFAULT_STEAM_PHOTO
            it.layoutX = 60.0
            it.layoutY = 12.0
            it.fitWidth = 36.0
            it.fitHeight = it.fitWidth
        }

        val username = Label(userModel.steam.accountName).also {
            it.id = USER_NAME_ID
            it.layoutX = 105.0
            it.layoutY = 14.0
        }

        val mode = Label().also {
            it.id = USER_MODE_ID
            it.text = ModeUtils.getEnabledMode(userModel.gameStat.enableDota, userModel.gameStat.enableCs)
            it.layoutX = 105.0
            it.layoutY = 32.0
        }

        val dotaStatus = viewGameStatus(GAME_DOTA_ID, userModel.gameStat.currentPlayedDota >= 100).also {
            it.layoutX = 325.0
        }

        val csStatus = viewGameStatus(GAME_CS_ID, userModel.gameStat.currentDroppedCs).also {
            it.layoutX = 400.0
        }

        pane.setOnMouseClicked { event ->
            if (event.button == MouseButton.PRIMARY) { selectUser(userModel.steam.accountName) }

            if (event.button == MouseButton.SECONDARY) {
                userInfoMenuView.setOffsetMenu(event.sceneX, event.sceneY)
                userInfoMenuView.setUserData(userModel, mode)

                dropUserModal.setUsers(currentUsers, selectedUsers)
                dropUserModal.setDropUser(userModel)

                prevUserMenu?.closeMenu()
                prevUserMenu = userInfoMenuView.apply { openMenu() }
            }
        }

        pane.children.addAll(select, photo, username, mode, dotaStatus, csStatus)
    }

    private fun viewGameStatus(imgId: String, value: Boolean) = Pane().also { pane ->
        pane.id = USER_GAME_STATUS_ID
        pane.layoutY = 15.0

        val icon = ImageView().also {
            it.id = imgId
            it.layoutX = 4.0
            it.layoutY = 3.0
        }

        val status = ImageView().also {
            it.id = if (value) "done" else "cross"
            it.layoutX = 30.0
            it.layoutY = if (value) 2.0 else 3.0
        }

        pane.children.addAll(icon, status)
    }

    private fun animateSequentially(nodes: List<Node>) {
        if (nodes.isEmpty()) return

        val firstNode = nodes.first()
        val remainingNodes = nodes.drop(1)

        val transition = ScaleTransition(Duration.millis(75.0), firstNode).also {
            it.fromX = 0.1
            it.fromY = 0.1
            it.toX = 1.0
            it.toY = 1.0
        }
        transition.setOnFinished {
            firstNode.scaleX = 1.0
            firstNode.scaleY = 1.0

            animateSequentially(remainingNodes)
        }
        transition.playFromStart()
    }

    private fun viewEdit(imdId: String) = Pane().also { pane ->
        pane.id = USER_VIEW_EDIT_ID
        pane.layoutY = 70.0
        pane.layoutX = when(imdId) {
            SELECT_ALL_ID -> 415.0
            EDIT_ID -> 445.0
            else -> 470.0
        }

        val icon = ImageView().also { it.id = imdId }
        pane.children.add(icon)
    }

    private fun selectUser(username: String) {
        selectedUsers[username]?.let {
            it.checkBox.id = USER_CHECKBOX_EMPTY
            selectedUsers.remove(username)
        } ?: run {
            currentUsers[username]?.let {
                it.checkBox.id = USER_CHECKBOX_SELECTED
                selectedUsers[username] = it
            }
        }
        changeIdSelect()
    }


    private fun selectAllUser() {
        selectedUsers = currentUsers.mapValues { (_, value) ->
            value.apply { checkBox.id = USER_CHECKBOX_SELECTED }
        }.toMutableMap()
        changeIdSelect()
    }

    private fun removeAllUser() {
        selectedUsers.values.forEach { value -> value.checkBox.id = USER_CHECKBOX_EMPTY }
        selectedUsers = mutableMapOf()
        changeIdSelect()
    }

    private fun changeIdSelect() {
        count.text = "${langApplication.text.accounts.numberOfAccounts}${currentUsers.size}"
        selected.text = "${langApplication.text.accounts.selected} ${selectedUsers.size}"
        selectAll.children[0].id = when (selectedUsers.isEmpty()) {
            true -> SELECT_ALL_ID
            else -> REMOVE_ALL_ID
        }
        Platform.runLater {
            selectAll.isDisable = currentUsers.isEmpty()
            trash.isDisable = selectedUsers.isEmpty()
            edit.isDisable = selectedUsers.isEmpty()
        }
    }

}

data class UserDataView(
    val userModel: UserModel,
    val userView: Pane,
    val checkBox: ImageView,
    val status: Label
)