package com.project.steamfarm.ui.view.section

import com.project.steamfarm.Runner
import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import com.project.steamfarm.repository.impl.PhotoRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.view.SectionType
import com.project.steamfarm.ui.view.block.account.NotFoundView
import com.project.steamfarm.ui.view.window.import.MaFileWindow
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
import javafx.scene.shape.Line
import javafx.util.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimerTask
import java.util.concurrent.CompletableFuture

val DEFAULT_PHOTO = Image(Runner::class.java.getResource("images/photo.png")!!.toURI().toString())

private const val USER_VIEW_ID = "userView"
private const val USER_CHECKBOX_EMPTY = "checkBox"
private const val USER_CHECKBOX_SELECTED = "selectedCheckBox"
private const val USER_NAME_ID = "userViewName"
private const val USER_MODE_ID = "userViewMode"
private const val USER_GAME_STATUS_ID = "userViewGameStatus"
private const val GAME_DOTA_ID = "dota"
private const val GAME_CS_ID = "cs"
private const val USER_VIEW_EDIT_ID = "userViewEdit"
private const val SELECT_ALL_ID = "selectAll"
private const val REMOVE_ALL_ID = "removeAll"
private const val EDIT_ID = "pencil"
private const val TRASH_ID = "bag"
private const val STATUA_ID = "statua"

private const val DOTA_NAME = "Dota 2"
private const val CS_NAME = "Counter-Strike 2"

private const val USER_VIEW_Y = 60.0

private const val USER_EDIT_MENU_ID = "editMenu"
private const val USER_EDIT_BUTTON_MENU_ID = "editMenuButton"
private const val USER_EDIT_MENU_TEXT_ID = "editMenuText"

private const val USER_MENU_VIEW_ID = "userMenuView"
private const val USER_MENU_HEROES_ID = "userMenuHeroesView"
private const val USER_MENU_TEXT_ID = "userMenuText"
private const val USER_MENU_HINT_ID = "userMenuTextHint"

private const val USER_MENU_DATE_ID = "date"
private const val USER_MENU_CLOCK_ID = "clock"
private const val USER_MENU_DROP_ID = "money"

private const val TIME_REFRESH_COUNT = 5

private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

class AccountSectionView: DefaultSectionView(SectionType.ACCOUNTS) {

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
        it.layoutY = 65.0//84.0
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

    private val edit = viewEdit(EDIT_ID).also {
        it.setOnMouseClicked { viewEditMenu() }
    }


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

        it.vvalueProperty().addListener { _, _, _ -> closePrevMenu() }
        it.hvalueProperty().addListener { _, _, _ -> closePrevMenu() }
    }

    private var prevMenu: Pane? = null

    private val userRepository = UserRepository()
    private val photoRepository = PhotoRepository()

    private val notFoundView = NotFoundView(content)

    private var users: MutableList<UserModel> = mutableListOf()
    private var userNodes: MutableList<Pane> = mutableListOf()

    private var selectedUser: MutableMap<String, Pane> = HashMap()

    override fun refreshLanguage() {
        search.promptText = langApplication.text.accounts.search
        val importText = import.children.firstOrNull { it.id == "importText" } as? Label
        importText?.text = langApplication.text.accounts.import
    }

    override fun initialize() {
        section.children.addAll(block, scroll/*, sorting*/, selected, count, selectAll, edit, trash)

        search.textProperty().addListener { _, _, newValue -> search(newValue)}
        import.setOnMouseClicked { _ -> MaFileWindow(this).show() }

        CompletableFuture.supplyAsync {
            users = userRepository.findByType(UserType.AUTH_COMPLETED).toMutableList()
            userNodes = users.map { viewUser(it) }.toMutableList()
            viewUsers(userNodes, true)
        }

        timer.schedule(RefreshAccount(), 1000, 1000)
        super.initialize()
    }

    private fun search(prefix: String) {

        val findUser = userNodes.filter {
            val node = it.children.firstOrNull { n -> n.id == USER_NAME_ID } ?: return@filter false
            val username = node as Label
            username.text.contains(prefix)
        }
        viewUsers(findUser)
    }

    private fun viewUsers(users: List<Pane>, isAnimate: Boolean = false) = Platform.runLater {

        content.children.clear()
        if (users.isNotEmpty()) {
            var vertical = 0

            users.forEach {
                if (!isAnimate) {
                    it.scaleX = 1.0
                    it.scaleY = 1.0
                }
                it.layoutY = USER_VIEW_Y * vertical++
            }

            count.text = "${langApplication.text.accounts.numberOfAccounts}${userNodes.size}"
            content.children.addAll(users)
            if (isAnimate) animateSequentially(users)
        } else {
            notFoundView.view()
        }
    }

    private fun appendUser(userModel: UserModel) = Platform.runLater {

        val view = viewUser(userModel)

        userNodes.add(view)
        users.add(userModel)

        count.text = "${langApplication.text.accounts.numberOfAccounts}${userNodes.size}"
        if (search.text.isEmpty() || (search.text.isNotEmpty() && userModel.username.contains(search.text))) {

            content.children.removeIf {
                it.id == notFoundView.logo.id || it.id == notFoundView.title.id || it.id == notFoundView.hint.id
            }

            view.layoutY = USER_VIEW_Y * content.children.size
            content.children.add(view)

            animateSequentially(listOf(view))
        }
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
            it.image = photoRepository.findById(userModel.username) ?: DEFAULT_PHOTO
            it.layoutX = 60.0
            it.layoutY = 12.0
            it.fitWidth = 36.0
            it.fitHeight = it.fitWidth
        }

        val username = Label(userModel.username).also {
            it.id = USER_NAME_ID
            it.layoutX = 105.0
            it.layoutY = 14.0
        }

        val mode = Label().also {
            it.id = USER_MODE_ID
            it.text = getEnabledMode(userModel.gameStat.enableDota, userModel.gameStat.enableCs)
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
            if (event.button == MouseButton.PRIMARY) {
                selectUser(userModel, pane)
            }

            if (event.button == MouseButton.SECONDARY) {
                userMenuView(userModel, event.sceneX, event.sceneY)
            }
        }

        pane.children.addAll(select, photo, username, mode, dotaStatus, csStatus)
    }

    private fun getEnabledMode(isDotaEnabled: Boolean, isCsEnabled: Boolean): String {

        if (!isDotaEnabled && !isCsEnabled) {
            return langApplication.text.accounts.unused
        }

        val stringBuilder = StringBuilder()
        if (isDotaEnabled) stringBuilder.append(DOTA_NAME)
        if (isCsEnabled) {
            if (stringBuilder.isNotEmpty()) stringBuilder.append(" | ")
            stringBuilder.append(CS_NAME)
        }
        return stringBuilder.toString()
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

    private fun selectUser(userModel: UserModel, pane: Pane) {
        val isSelected = selectedUser[userModel.username] != null
        if (isSelected) selectedUser.remove(userModel.username) else selectedUser[userModel.username] = pane

        pane.children.firstOrNull { it.id == USER_CHECKBOX_SELECTED || it.id == USER_CHECKBOX_EMPTY }?.let {
            it.id = if (isSelected) USER_CHECKBOX_EMPTY else USER_CHECKBOX_SELECTED
        }
        changeIdSelect()
    }

    private fun selectAllUser() {
        userNodes.forEach {

            it.children.firstOrNull { n -> n.id == USER_CHECKBOX_SELECTED || n.id == USER_CHECKBOX_EMPTY }?.let { i ->
                i.id = USER_CHECKBOX_SELECTED
            }

            val node = it.children.firstOrNull { n -> n.id == USER_NAME_ID }
            val username = node as Label

            val userModel = users.firstOrNull { u -> u.username == username.text }
            if (userModel != null) {
                selectedUser[userModel.username] = it
            }
        }
        changeIdSelect()
    }

    private fun removeAllUser() {
        selectedUser.entries.forEach {
            it.value.children.firstOrNull { n -> n.id == USER_CHECKBOX_SELECTED }?.let { i ->
                i.id = USER_CHECKBOX_EMPTY
            }
        }
        selectedUser.clear()
        changeIdSelect()
    }

    private fun changeIdSelect() = Platform.runLater {
        selected.text = "${langApplication.text.accounts.selected} ${selectedUser.size}"
        selectAll.children[0].id = if (selectedUser.size == users.size) REMOVE_ALL_ID else SELECT_ALL_ID
    }

    private fun closePrevMenu() = prevMenu?.let {
        root.children.removeAll(it)
        prevMenu = null
    }

    private fun viewEditMenu() = Platform.runLater {

        closePrevMenu()
        val menu = Pane().also {
            it.id = USER_EDIT_MENU_ID
            it.layoutX = 525.0
            it.layoutY = 120.0
        }

        // Button: Add accounts to farm dota 2
        viewButton(GAME_DOTA_ID, langApplication.text.accounts.action.enableFarmGame).let {
            it.setOnMouseClicked { updateUsers(isDota = true, isEnabled = true) }
            menu.children.add(it)
        }
        // Button: Remove accounts from farm dota 2
        viewButton(GAME_DOTA_ID, langApplication.text.accounts.action.disableFarmGame).let {
            it.layoutY = 40.0
            it.setOnMouseClicked { updateUsers(isDota = true, isEnabled = false) }
            menu.children.add(it)
        }

        Line().also {
            it.layoutX = 100.0
            it.layoutY = 80.0
            it.startX = -100.0
            it.endX = 100.0

            menu.children.add(it)
        }

        // Button: Add accounts to farm cs 2
        viewButton(GAME_CS_ID, langApplication.text.accounts.action.enableFarmGame).let {
            it.layoutY = 81.0
            it.setOnMouseClicked { updateUsers(isDota = false, isEnabled = true) }
            menu.children.add(it)
        }
        // Button: Remove accounts from farm cs 2
        viewButton(GAME_CS_ID, langApplication.text.accounts.action.disableFarmGame).let {
            it.layoutY = 121.0
            it.setOnMouseClicked { updateUsers(isDota = false, isEnabled = false) }
            menu.children.add(it)
        }

        prevMenu = menu
        menu.setOnMouseExited { closePrevMenu() }

        root.children.add(menu)
    }

    private fun updateUsers(isDota: Boolean, isEnabled: Boolean) = users.filter { selectedUser.containsKey(it.username) }
        .forEach {
            val user = selectedUser[it.username] ?: return@forEach

            if (isDota) it.gameStat.enableDota = isEnabled else it.gameStat.enableCs = isEnabled
            userRepository.save(it)

            user.children.firstOrNull { n -> n.id == USER_MODE_ID }?.let { node ->
                val mode = node as Label
                mode.text = getEnabledMode(it.gameStat.enableDota, it.gameStat.enableCs)
            }
        }

    private fun userMenuView(userModel: UserModel, offsetX: Double, offsetY: Double) = Platform.runLater {

        closePrevMenu()
        val menu = Pane().also {
            it.id = USER_MENU_VIEW_ID
        }

        menu.boundsInLocalProperty().addListener { _, _, bounds ->
            menu.layoutX = offsetX - bounds.width
            menu.layoutY = if (offsetY + bounds.height > root.scene.window.height) {
                root.scene.window.height - bounds.height - 10.0
            } else offsetY
        }

        val yourHeroes = Label(langApplication.text.accounts.action.yourHero).also {
            it.id = USER_MENU_HINT_ID
            it.layoutX = 10.0
            it.layoutY = 4.0
        }

        val heroes = Pane().also {
            it.id = USER_MENU_HEROES_ID
            it.layoutY = 20.0
        }

        val createdDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(userModel.createdTs), ZoneId.systemDefault())
        setStatisticToMenu(menu, USER_MENU_DATE_ID, createdDate.format(formatter))

        val clock = "${userModel.gameStat.currentPlayedDota} ${langApplication.text.accounts.action.hours}"
        setStatisticToMenu(menu, USER_MENU_CLOCK_ID, clock)

        val drop = if (userModel.gameStat.lastDropCsDate != null) userModel.gameStat.lastDropCsDate!!.format(formatter)
            else langApplication.text.accounts.action.unknown

        setStatisticToMenu(menu, USER_MENU_DROP_ID, drop)

        val firstLine = Line().also {
            it.layoutX = 100.0
            it.layoutY = 164.0
            it.startX = -100.0
            it.endX = 100.0
        }

        val changeHero = viewButton(STATUA_ID, langApplication.text.accounts.action.chooseHero).also {
            it.layoutY = 165.0
        }

        val dotaStatus = if (userModel.gameStat.enableDota) langApplication.text.accounts.action.disableFarmGame
        else langApplication.text.accounts.action.enableFarmGame

        val changeDotaFarm = viewButton(GAME_DOTA_ID, dotaStatus).also {
            it.layoutY = 205.0
        }

        val csStatus = if (userModel.gameStat.enableDota) langApplication.text.accounts.action.disableFarmGame
        else langApplication.text.accounts.action.enableFarmGame

        val changeCsFarm = viewButton(GAME_CS_ID, csStatus).also {
            it.layoutY = 245.0
        }

        val secondLine = Line().also {
            it.layoutX = 100.0
            it.layoutY = 285.0
            it.startX = -100.0
            it.endX = 100.0
        }

        val dropAccount = viewButton(USER_MENU_DROP_ID, langApplication.text.accounts.action.dropAccount).also {
            it.layoutY = 286.0
        }

        menu.children.addAll(yourHeroes, heroes, changeHero, firstLine, changeDotaFarm, changeCsFarm, secondLine, dropAccount)
        menu.setOnMouseExited { closePrevMenu() }

        prevMenu = menu
        root.children.add(menu)

        val scaleTransition = ScaleTransition(Duration.millis(150.0), menu).also {
            menu.scaleX = 0.1
            menu.scaleY = 0.1

            it.fromX = 0.1
            it.fromY = 0.1
            it.toX = 1.0
            it.toY = 1.0
        }
        scaleTransition.play()
    }

    private fun setStatisticToMenu(pane: Pane, id: String, value: String) = Platform.runLater {

        val icon = ImageView().also {
            it.id = id
            it.layoutX = 14.0
            it.layoutY = when(id) {
                USER_MENU_DATE_ID -> 68.0
                USER_MENU_CLOCK_ID -> 102.0
                else -> 134.0
            }
        }

        val text = Label(value).also {
            it.id = USER_MENU_TEXT_ID
            it.layoutX = 45.0
            it.layoutY = icon.layoutY - 2
        }

        val hint = Label().also {
            it.id = USER_MENU_HINT_ID
            it.layoutX = text.layoutX
            it.layoutY = icon.layoutY + 12.0
            it.text = when(id) {
                USER_MENU_DATE_ID -> langApplication.text.accounts.action.createdDate
                USER_MENU_CLOCK_ID -> langApplication.text.accounts.action.clockInGame
                else -> langApplication.text.accounts.action.lastDropDate
            }
        }

        pane.children.addAll(icon, text, hint)
    }

    private fun viewButton(iconId: String, value: String) = Pane().also {
        it.id = USER_EDIT_BUTTON_MENU_ID

        val icon = ImageView().also { img ->
            img.id = iconId
            img.layoutX = 10.0
            img.layoutY = 7.0
        }

        val text = Label().also { l ->
            l.text = value
            l.id = USER_EDIT_MENU_TEXT_ID
            l.layoutX = 42.0
            l.layoutY = 10.0
        }

        it.children.addAll(icon, text)
    }

    inner class RefreshAccount: TimerTask() {

        private var count = TIME_REFRESH_COUNT

        override fun run() {
            count--

            if (count <= 0) {

                userRepository.findAll()
                    .filter { it.userType == UserType.AUTH_COMPLETED && !users.contains(it) }
                    .forEach { appendUser(it) }

                count = TIME_REFRESH_COUNT
            }
        }
    }

}