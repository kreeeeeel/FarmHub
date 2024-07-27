package com.project.panel.ui.view.modal

import com.project.panel.AppRun
import com.project.panel.langApplication
import com.project.panel.model.HeroModel
import com.project.panel.model.UserModel
import com.project.panel.repository.impl.HeroImageRepository
import com.project.panel.repository.impl.HeroRepository
import com.project.panel.repository.impl.UserRepository
import com.project.panel.ui.controller.BaseController.Companion.root
import com.project.panel.ui.view.menu.user.STATUA_ID
import javafx.application.Platform
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import javafx.scene.text.Text

private const val HERO_PRIORITY_BLOCK_ID = "peekHeroBlock"
private const val HERO_PEEKED_ID = "peekedHero"
private const val HERO_PEEKED_NAME_ID = "peekedHeroName"
private const val HERO_SELECTED_PEEKED_ID = "selectedPeekedHero"

private const val HERO_SEARCH_BLOCK_ID = "searchHeroBlock"
private const val HERO_SEARCH_ID = "searchHero"
private const val HERO_SEARCH_FIELD_ID = "searchHeroField"
private const val HERO_COUNT_ID = "countHeroes"
private const val HERO_FIELD_ID = "hero"

private const val HERO_HINT_ID = "peekHeroHint"

val DEFAULT_RANDOM_PHOTO = Image(
    AppRun::class.java.getResource("images/random.png")!!
        .toURI()
        .toString()
)

class HeroModal: DefaultModal() {

    private val priority = Pane().also {
        it.id = HERO_PRIORITY_BLOCK_ID
        it.layoutX = 110.0
        it.layoutY = 100.0

        val icon = ImageView().also { img ->
            img.id = STATUA_ID
            img.layoutX = 14.0
            img.layoutY = 14.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        val text = Label(langApplication.text.accounts.hero.title).also { l ->
            l.layoutX = 46.0
            l.layoutY = 16.0
        }

        it.children.addAll(icon, text)
    }

    private val search = Pane().also {
        it.id = HERO_SEARCH_BLOCK_ID
        it.layoutX = 360.0
        it.layoutY = 100.0
    }

    private val searchInner = Pane().also { b ->
        b.id = HERO_SEARCH_ID
        b.layoutX = 17.0
        b.layoutY = 14.0

        val icon = ImageView().also { img ->
            img.id = "search"
            img.layoutX = 14.0
            img.layoutY = 8.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        b.children.add(icon)
    }

    private val searchField = TextField().also {
        it.id = HERO_SEARCH_FIELD_ID
        it.isFocusTraversable = false
        it.promptText = langApplication.text.accounts.hero.search
        it.layoutX = 41.0
        it.layoutY = 4.0

        it.textProperty().addListener { _, _, newValue -> search(newValue)}
    }

    private val count = Label().also {
        it.id = HERO_COUNT_ID
        it.layoutX = 17.0
        it.layoutY = 60.0
    }

    private val content = AnchorPane().also { ap ->
        ap.prefWidth = 314.0
        ap.prefHeight = 315.0
    }

    private val scroll = ScrollPane().also {
        it.layoutY = 77.0
        it.prefWidth = 330.0
        it.prefHeight = 320.0
        it.content = content
    }

    private val hint = Label(langApplication.text.accounts.hero.hint).also { l ->
        l.id = HERO_HINT_ID
        l.layoutX = 80.0
        l.layoutY = 510.0
    }

    private lateinit var currentHeroes: MutableMap<HeroModel, Pane>
    private lateinit var currentPriorityHeroes: MutableList<HeroDataView>

    private var selectedPriorityHeroIndex: Int = 0

    private lateinit var userModel: UserModel

    private var prevInfoHero: Label? = null

    override fun show() {

        searchInner.children.add(searchField)
        search.children.addAll(searchInner, count, scroll)
        window.children.addAll(priority, search, hint)

        currentPriorityHeroes = userModel.gameStat.priorityHero.mapIndexed { index, it ->
            HeroDataView(it, Pane(), getPriorityView(HeroRepository.findById(it), index).apply { layoutY = 55.0 + 55.0*index  })
        }.toMutableList().also { viewPriorityHeroes() }

        currentHeroes = HeroRepository.findAll().associateWith { getHeroView(it) }
            .toMutableMap().also {
                count.text = "${langApplication.text.accounts.hero.count}${it.size}"
                viewHeroes(it.values.toList(), true)
            }

        super.show()
    }

    fun setUserDate(userModel: UserModel) { this.userModel = userModel }

    private fun search(prefix: String) = viewHeroes(
        currentHeroes.filter { getCorrectHeroName(it.key.name).contains(getCorrectHeroName(prefix)) }
            .map { it.value }
    )

    private fun viewPriorityHeroes() = Platform.runLater {
        changePriority(selectedPriorityHeroIndex)

        priority.children.addAll(currentPriorityHeroes.map { it.priorityHero })
        animateFadeTransition(priority.children, 75.0)
    }

    private fun viewHeroes(heroes: List<Pane>, isAnimate: Boolean = false) = Platform.runLater {

        content.children.clear()
        if (heroes.isNotEmpty()) {
            var vertical = 0
            var horizontal = 0

            heroes.map {
                it.layoutX = 17.0 + 60 * horizontal++
                it.layoutY = 10.0 + 60 * vertical

                if (horizontal >= 5) {
                    horizontal = 0
                    vertical++
                }
            }
            content.children.addAll(heroes)
            if (isAnimate) animateFadeTransition(heroes, 10.0)
        } else {

            val logo = ImageView().also {
                it.id = "404"
                it.fitWidth = 96.0
                it.fitHeight = 96.0
                it.layoutX = 110.0
                it.layoutY = 63.0
            }

            val title = Label(langApplication.text.accounts.hero.notFound).also {
                it.prefWidth = 200.0
                it.alignment = Pos.CENTER
                it.layoutX = 65.0
                it.layoutY = 180.0
            }

            content.children.addAll(logo, title)
        }

    }

    private fun getHeroView(heroModel: HeroModel) = Pane().also {
        it.id = HERO_FIELD_ID

        currentPriorityHeroes.firstOrNull { hero -> hero.name == getCorrectHeroName(heroModel.name) }
            ?.let { v ->
                it.isDisable = true
                v.hero = it
            }

        val icon = ImageView().also { img ->
            img.image = HeroImageRepository.findById(heroModel.icon)
            img.layoutX = 9.0
            img.layoutY = 9.0
        }

        it.setOnMouseClicked { event -> if (event.button == MouseButton.PRIMARY) changePriorityHero(heroModel, it) }
        it.children.add(icon)

        viewNameHero(heroModel.name, it)
    }

    private fun getPriorityView(heroModel: HeroModel?, index: Int): Pane = Pane().also {
        it.id = HERO_PEEKED_ID
        it.layoutX = 14.0

        var image = DEFAULT_RANDOM_PHOTO

        if (heroModel != null) {
            HeroImageRepository.findById(heroModel.icon)?.let { i -> image = i }
        }

        val icon = ImageView().also { img ->
            img.image = image
            img.layoutX = 14.0
            img.layoutY = 9.0
        }

        val label = Label().also { l ->
            l.text = if (image == DEFAULT_RANDOM_PHOTO) langApplication.text.accounts.hero.random else heroModel?.name
            l.id = HERO_PEEKED_NAME_ID
            l.layoutX = 55.0
            l.layoutY = 15.0
        }

        val clear = Pane().also { p ->
            p.isVisible = false
            p.layoutX = 180.0
            p.layoutY = -10.0
            p.prefWidth = 32.0
            p.prefHeight = 32.0

            val clearImg = ImageView().also { img ->
                img.id = "clear"
                img.fitWidth = p.prefWidth
                img.fitHeight = p.prefHeight
            }

            p.setOnMouseClicked { event ->
                clearPriorityHero(index)
                event.consume()
            }
            p.children.add(clearImg)
        }

        it.setOnMouseEntered { clear.isVisible = true && heroModel != null }
        it.setOnMouseExited { clear.isVisible = false }
        it.setOnMouseClicked { event ->
            changePriority(index)
            event.consume()
        }
        it.children.addAll(icon, label, clear)
    }

    private fun changePriority(index: Int) {
        currentPriorityHeroes[selectedPriorityHeroIndex].priorityHero.id = HERO_PEEKED_ID
        currentPriorityHeroes[index].priorityHero.id = HERO_SELECTED_PEEKED_ID
        selectedPriorityHeroIndex = index
    }

    private fun changePriorityHero(heroModel: HeroModel, hero: Pane) = Platform.runLater {

        val priorityView = getPriorityView(heroModel, selectedPriorityHeroIndex).also {
            it.id = HERO_SELECTED_PEEKED_ID

            it.layoutX = currentPriorityHeroes[selectedPriorityHeroIndex].priorityHero.layoutX
            it.layoutY = currentPriorityHeroes[selectedPriorityHeroIndex].priorityHero.layoutY

            priority.children.remove(currentPriorityHeroes[selectedPriorityHeroIndex].priorityHero)
            priority.children.add(it)
        }
        currentPriorityHeroes[selectedPriorityHeroIndex].hero.isDisable = false

        currentPriorityHeroes[selectedPriorityHeroIndex].hero = hero
        currentPriorityHeroes[selectedPriorityHeroIndex].hero.isDisable = true
        currentPriorityHeroes[selectedPriorityHeroIndex].priorityHero = priorityView

        userModel.gameStat.priorityHero[selectedPriorityHeroIndex] = getCorrectHeroName(heroModel.name)
        UserRepository.save(userModel)
    }

    private fun viewNameHero(name: String, target: Pane) {

        val text = Text(name).also {
            it.font = Font.font("Franklin Gothic Medium", 17.0)
        }

        val label = Label(name).also {
            it.id = "infoHero"
            it.prefWidth = text.layoutBounds.width + 25.0
            it.prefHeight = text.layoutBounds.height + 15.0
        }

        target.setOnMouseExited { closePrevInfoHero() }
        target.setOnMouseEntered { event ->

            val sceneX = event.sceneX
            val sceneY = event.sceneY

            val rootScene = root.localToScene(Point2D(0.0, 0.0))
            val targetScene = target.localToScene(Point2D(0.0, 0.0))

            label.layoutX = sceneX - label.prefWidth - (sceneX - targetScene.x - rootScene.x) - 10
            label.layoutY = sceneY - (sceneY - targetScene.y - rootScene.y) + 10

            prevInfoHero = label
            root.children.add(label)
        }
    }

    private fun closePrevInfoHero() = prevInfoHero?.let {
        root.children.remove(it)
        prevInfoHero = null
    }

    private fun clearPriorityHero(index: Int) = Platform.runLater {

        val priorityView = getPriorityView(null, index).also {
            it.layoutX = currentPriorityHeroes[index].priorityHero.layoutX
            it.layoutY = currentPriorityHeroes[index].priorityHero.layoutY

            priority.children.remove(currentPriorityHeroes[index].priorityHero)
            priority.children.add(it)
        }

        currentPriorityHeroes[index].priorityHero = priorityView
        if (selectedPriorityHeroIndex == index) {
            currentPriorityHeroes[index].priorityHero.id = HERO_SELECTED_PEEKED_ID
        }

        currentPriorityHeroes[index].name = "default"
        currentPriorityHeroes[index].hero.isDisable = false
        currentPriorityHeroes[index].priorityHero = priorityView

        userModel.gameStat.priorityHero[index] = "default"
        UserRepository.save(userModel)
    }

    private fun getCorrectHeroName(name: String) = name.trim()
        .replace(" ", "_")
        .replace("-", "_")
        .lowercase()

}

data class HeroDataView(
    var name: String,
    var hero: Pane,
    var priorityHero: Pane
)