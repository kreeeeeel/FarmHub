package com.project.steamfarm.ui.view.section

import com.project.steamfarm.Runner
import com.project.steamfarm.data.TimerData
import com.project.steamfarm.data.TimerType
import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import com.project.steamfarm.repository.Repository
import com.project.steamfarm.repository.impl.PhotoRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.view.SectionType
import com.project.steamfarm.ui.view.block.account.AccountMenuView
import com.project.steamfarm.ui.view.block.account.NotFoundView
import com.project.steamfarm.ui.view.window.import.MaFileWindow
import javafx.animation.FadeTransition
import javafx.animation.ScaleTransition
import javafx.application.Platform
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import javafx.util.Duration
import java.util.*
import java.util.concurrent.CompletableFuture


const val CONTENT_HEIGHT = 456.0

val DEFAULT_PHOTO = Image(Runner::class.java.getResource("images/photo.png")!!.toURI().toString())

private const val USER_DEFAULT_VIEW = "userDefaultView"
private const val USER_FAILURE_VIEW = "userFailureView"
private const val USER_BLOCK_VIEW = "userBlockView"

private const val DOTA_VIEW = "dotaView"
private const val CS_VIEW = "csView"

private const val USERNAME_FIELD_AUTH = "userAuthUsername"
private const val USERNAME_FIELD_COMPLETED = "userCompletedName"

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

    private val content = AnchorPane().also { ap ->
        ap.prefWidth = 505.0
        ap.prefHeight = CONTENT_HEIGHT

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
        it.layoutY = 85.0
        it.prefWidth = 520.0
        it.prefHeight = 458.0
        it.content = content

        it.vvalueProperty().addListener { _, _, _ -> closePrevMenu() }
        it.hvalueProperty().addListener { _, _, _ -> closePrevMenu() }
    }

    private val notFoundView = NotFoundView(content)

    private val photoRepository: Repository<Image> = PhotoRepository()
    private val userRepository: Repository<UserModel> = UserRepository()

    private var nodes: MutableList<Pane> = mutableListOf()
    private var prevMenu: Pane? = null

    override fun refreshLanguage() {
        search.promptText = langApplication.text.accounts.search
        val importText = import.children.firstOrNull { it.id == "importText" } as? Label
        importText?.text = langApplication.text.accounts.import
    }

    override fun initialize() {
        section.children.addAll(block, scroll)

        search.textProperty().addListener { _, _, newValue -> search(newValue)}
        import.setOnMouseClicked { _ -> MaFileWindow(this).show() }

        CompletableFuture.supplyAsync {
            nodes = userRepository.findAll().map { viewUser(it) }.toMutableList()
            viewUsers(nodes, true)
        }

        super.initialize()
    }

    fun refreshUi(users: List<UserModel>) {
        this.nodes = users.map { viewUser(it) }.toMutableList()
        viewUsers(this.nodes)
    }

    private fun search(prefix: String) {
        val values = nodes.filter {
            val block = it.children[0] as Pane
            val node = block.children.firstOrNull { n ->
                n.id == USERNAME_FIELD_AUTH || n.id == USERNAME_FIELD_COMPLETED
            } ?: return@filter false

            val username = node as Label
            username.text.contains(prefix)
        }
        viewUsers(values)
    }

    private fun viewUsers(nodes: List<Pane>, isAnimation: Boolean = false) = Platform.runLater {

        closePrevMenu()
        content.children.clear()
        if (nodes.isNotEmpty()) {

            var vertical = 0
            var horizontal = 0

            val result = nodes.map {
                it.also { u ->
                    u.layoutX = 14.0 + 250 * horizontal++
                    u.layoutY = 7.0 + 128 * vertical
                    u.opacity = if (isAnimation) 0.0 else 1.0

                    if (horizontal == 2) {
                        horizontal = 0
                        vertical++
                    }
                }
            }
            content.children.addAll(result)
            if (isAnimation) animateSequentially(nodes)
        } else notFoundView.view()
    }

    private fun viewUser(user: UserModel): Pane = when(user.userType) {
        UserType.WAIT_AUTH -> viewUserWithTimer(user)
        UserType.BAD_AUTH -> viewUserFailure(user)
        UserType.AUTH_COMPLETED -> viewUserCompleted(user)
    }

    private fun viewUserWithTimer(user: UserModel): Pane = Pane().also { pane ->
        pane.id = USER_DEFAULT_VIEW
        val block = Pane().also { b ->
            b.id = USER_BLOCK_VIEW
            b.layoutX = 5.0
            b.layoutY = 5.0
        }

        val icon = ImageView().also { img ->
            img.id = "time"
            img.layoutX = 74.0
            img.layoutY = 14.0
        }

        val timer = Label().also {
            it.text = getTime(user.createdTs)
            it.id = "userTimer"
            it.layoutX = 100.0
            it.layoutY = 16.0
        }

        val username = Label(user.username).also {
            it.id = "userAuthUsername"
            it.layoutY = 55.0
        }

        val authorization = Label(langApplication.text.accounts.authorization.name).also {
            it.id = "userAuthAuthorization"
            it.layoutY = 75.0
        }

        block.children.addAll(icon, timer, username, authorization)
        pane.children.add(block)

        val currentTimer = Timer()
        val currentTimerTask = UpdateTime(timer, pane, user)
        val currentTimerData = TimerData(currentTimer, user.username, TimerType.WAIT_AUTH)

        currentTimer.schedule(currentTimerTask, 1000, 1000)
        startTask(currentTimerData)
    }

    private fun viewUserFailure(user: UserModel): Pane = Pane().also { pane ->
        pane.id = USER_FAILURE_VIEW

        val block = Pane().also { b ->
            b.id = USER_BLOCK_VIEW
            b.layoutX = 5.0
            b.layoutY = 5.0
        }

        val cross = ImageView().also { img ->
            img.id = "cross"
            img.layoutX = 8.0
            img.layoutY = 8.0
        }

        val failure = Label(langApplication.text.failure.name).also {
            it.id = "userFailure"
            it.layoutX = 36.0
            it.layoutY = 11.0
        }

        val username = Label(user.username).also {
            it.id = "userAuthUsername"
            it.layoutY = 42.0
        }

        val authorization = Label(langApplication.text.accounts.login).also {
            it.id = "userAuthAuthorization"
            it.layoutY = 58.0
        }

        val comment = Label(langApplication.text.accounts.authorization.badAuth).also {
            it.id = "userFailureComment"
            it.layoutY = 80.0
        }

        block.children.addAll(cross, failure, username, authorization, comment)
        pane.children.add(block)
    }

    private fun viewUserCompleted(user: UserModel): Pane = Pane().also { pane ->
        pane.id = USER_DEFAULT_VIEW
        pane.cursor = Cursor.HAND

        val block = Pane().also { b ->
            b.id = USER_BLOCK_VIEW
            b.layoutX = 5.0
            b.layoutY = 5.0
        }

        val image = photoRepository.findById(user.username) ?: DEFAULT_PHOTO
        val photo = Circle().also {
            it.radius = 24.0
            it.fill = ImagePattern(image)
            it.layoutX = 38.0
            it.layoutY = 38.0
        }

        val username = Label(user.username).also {
            it.id = "userCompletedName"
            it.layoutX = 73.0
            it.layoutY = 20.0
        }

        val login = Label(langApplication.text.accounts.login).also {
            it.id = "userCompletedLogin"
            it.layoutX = 73.0
            it.layoutY = 38.0
        }

        val dota = viewDota(user)
        val cs = viewCs(user)

        block.children.addAll(photo, username, login, dota, cs)
        pane.children.add(block)

        pane.setOnMouseClicked { event ->
            viewMenu(user, pane.layoutX, event.sceneY)
        }
    }

    private fun viewDota(user: UserModel): Pane = Pane().also { pane ->
        pane.id = DOTA_VIEW
        pane.isDisable = user.gameStat.enableDota != true
        pane.layoutX = 18.0
        pane.layoutY = 70.0

        val icon = ImageView().also { img ->
            img.id = "dota"
            img.layoutX = 10.0
            img.layoutY = 3.0
        }

        val value = Label().also {
            it.id = "dotaValue"
            it.text = "${user.gameStat.currentPlayedDota} ${langApplication.text.hour}"
            it.layoutX = 40.0
            it.layoutY = 6.0
        }

        pane.children.addAll(icon, value)
    }

    private fun viewCs(user: UserModel): Pane = Pane().also { pane ->
        pane.id = CS_VIEW
        pane.isDisable = user.gameStat.enableCs != true
        pane.layoutX = 132.0
        pane.layoutY = 70.0

        val icon = ImageView().also { img ->
            img.id = "cs"
            img.layoutX = 10.0
            img.layoutY = 3.0
        }

        val value = ImageView().also { img ->
            img.id = when(user.gameStat.currentDroppedCs) {
                true -> "done"
                false -> "cross"
            }
            img.layoutX = 40.0
            img.layoutY = 3.0
        }
        pane.children.addAll(icon, value)
    }

    private fun getTime(time: Long): String {
        val avg = (System.currentTimeMillis() - time) / 1000
        val minutes = avg / 60
        val seconds = avg % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun animateSequentially(nodes: List<Node>) {
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

    private fun viewMenu(user: UserModel, offsetX: Double, offsetY: Double) {

        prevMenu?.let { root.children.remove(it) }
        prevMenu = null

        val menu = AccountMenuView().view(user).also {
            it.layoutX = section.layoutX + offsetX - it.boundsInLocal.width - 5.0
            it.layoutY = if(offsetY + it.boundsInLocal.height > root.scene.window.height) {
                root.scene.window.height - it.boundsInLocal.height - 10.0
            } else offsetY
            it.scaleX = 0.1
            it.scaleY = 0.1

            root.children.add(it)
        }

        val scaleTransition = ScaleTransition(Duration.millis(150.0), menu).also {
            it.fromX = 0.1
            it.fromY = 0.1
            it.toX = 1.0
            it.toY = 1.0
        }
        scaleTransition.play()
        prevMenu = menu

        menu.setOnMouseExited { closePrevMenu() }
    }

    private fun closePrevMenu() = prevMenu?.let {
        root.children.remove(it)
        prevMenu = null
    }

    inner class UpdateTime(
        private val timer: Label,
        private val userView: Pane,
        private val currentUser: UserModel
    ): TimerTask() {

        override fun run() = Platform.runLater {
            timer.text = getTime(currentUser.createdTs)
            userRepository.findById(currentUser.username)?.let { if (it.userType != UserType.WAIT_AUTH) replace(it) }
        }

        private fun replace(user: UserModel) {
            val indexOfNodes = nodes.indexOf(userView)
            val indexOfContent = content.children.indexOf(userView)

            val newUserView = viewUser(user).also { v ->
                v.layoutX = userView.layoutX
                v.layoutY = userView.layoutY
            }

            if (indexOfNodes != -1) {
                nodes.removeAt(indexOfNodes)
                nodes.add(newUserView)
            }

            if (indexOfContent != -1) {
                content.children.removeAt(indexOfContent)
                content.children.add(newUserView)
            }

            finishTask(user.username, TimerType.WAIT_AUTH)
        }

    }

}