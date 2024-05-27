package com.project.steamfarm.ui.view.section

import com.project.steamfarm.data.TimerType
import com.project.steamfarm.langApplication
import com.project.steamfarm.model.ConfigModel
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.repository.Repository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.view.SectionType
import com.project.steamfarm.ui.view.block.account.view.AccountCardView
import com.project.steamfarm.ui.view.block.account.view.AccountListView
import com.project.steamfarm.ui.view.block.account.AccountView
import com.project.steamfarm.ui.view.window.import.MaFileWindow
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import java.util.concurrent.CompletableFuture

private const val VIEW_ID = "view"
private const val VIEW_DISABLED_ID = "viewDisable"

const val CONTENT_HEIGHT = 427.0

class AccountSectionView: DefaultSectionView(SectionType.ACCOUNTS) {

    private val config = ConfigModel().fromFile()

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

    private val list = userView(true)
    private val table = userView(false)

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
        it.prefHeight = 430.0
        it.content = content
    }

    private var accountView: AccountView = if (config.userViewIsList) {
        AccountListView(content)
    }
    else AccountCardView(scroll, content)

    private val userRepository: Repository<UserModel> = UserRepository()
    private var users: List<UserModel> = userRepository.findAll()

    override fun refreshLanguage() {
        search.promptText = langApplication.text.accounts.search
        val importText = import.children.firstOrNull { it.id == "importText" } as? Label
        importText?.text = langApplication.text.accounts.import
    }

    override fun initialize() {
        section.children.addAll(block, list, table, scroll)

        search.textProperty().addListener { _, _, newValue -> accountView.search(newValue)}
        import.setOnMouseClicked { _ -> MaFileWindow(this).show() }

        CompletableFuture.supplyAsync { accountView.view(users) }

        super.initialize()
    }

    fun refreshUi(users: List<UserModel>) {
        this.users = users
        accountView.view(users)
    }

    private fun userView(isList: Boolean) = Pane().also {
        it.id = if (isList == config.userViewIsList) VIEW_DISABLED_ID else VIEW_ID
        it.layoutX = if (isList) 430.0 else 460.0
        it.layoutY = 60.0
        it.isDisable = isList == config.userViewIsList

        val icon = ImageView().also { img ->
            img.id = if (isList) "list" else "table"
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        it.setOnMouseClicked { _ ->
            config.userViewIsList = isList
            config.save()

            section.children.firstOrNull { node -> node.id == VIEW_DISABLED_ID }?.let { l ->
                l.id = VIEW_ID
                l.isDisable = false

                it.id = VIEW_DISABLED_ID
                it.isDisable = true
            }

            accountView = if (isList) AccountListView(content) else AccountCardView(scroll, content)
            CompletableFuture.supplyAsync {
                finishTask(TimerType.WAIT_AUTH)

                accountView.view(users)
                if (search.text.isNotEmpty()) accountView.search(search.text)
            }
        }
        it.children.add(icon)
    }

}