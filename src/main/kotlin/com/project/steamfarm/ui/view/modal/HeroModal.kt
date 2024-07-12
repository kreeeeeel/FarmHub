package com.project.steamfarm.ui.view.modal

import com.project.steamfarm.Runner
import com.project.steamfarm.langApplication
import com.project.steamfarm.model.HeroModel
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.repository.impl.HeroImageRepository
import com.project.steamfarm.repository.impl.HeroRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.view.menu.user.STATUA_ID
import javafx.application.Platform
import javafx.geometry.Point2D
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

val DEFAULT_PHOTO = Image(Runner::class.java.getResource("images/random.png")!!.toURI().toString())

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

    private var currentPriorityHeroes: MutableList<Pane> = MutableList(6) { Pane() }
    private var usedHeroes: MutableList<Pane> = MutableList(6) { Pane() }

    private lateinit var selectedPriorityHero: Pane
    private var selectedPriorityHeroIndex: Int = -1

    private lateinit var userModel: UserModel

    private var prevInfoHero: Label? = null

    override fun show() {

        searchInner.children.add(searchField)
        search.children.addAll(searchInner, count, scroll)
        window.children.addAll(priority, search, hint)

        currentHeroes = HeroRepository.findAll().associateWith { getHeroView(it) }.toMutableMap()

        count.text = "${langApplication.text.accounts.hero.count}${currentHeroes.size}"

        viewPriorityHeroes()
        viewHeroes(currentHeroes.values.toList())

        super.show()
    }

    fun setUserDate(userModel: UserModel) { this.userModel = userModel }

    private fun search(prefix: String) = viewHeroes(
        currentHeroes.filter { getCorrectHeroName(it.key.name).contains(getCorrectHeroName(prefix)) }
            .map { it.value }
    )

    private fun viewPriorityHeroes() = Platform.runLater {
        currentPriorityHeroes = List(currentPriorityHeroes.size) { index ->
            getPriorityView(HeroRepository.findById(userModel.gameStat.priorityHero[index]))
                .also {
                    it.layoutY = 55.0 + 55.0*index
                }
        }.toMutableList()

        selectedPriorityHero = currentPriorityHeroes[0]
        selectedPriorityHero.id = HERO_SELECTED_PEEKED_ID

        selectedPriorityHeroIndex = 0

        priority.children.addAll(currentPriorityHeroes)
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
            //content.prefHeight = vertical * 60.0 + 80.0
            if (isAnimate) animateFadeTransition(heroes)
        } else {

            val logo = ImageView().also {
                it.id = "404"
                it.fitWidth = 96.0
                it.fitHeight = 96.0
                it.layoutX = 110.0
                it.layoutY = 63.0
            }

            val title = Label(langApplication.text.accounts.hero.notFound).also {
                it.layoutX = 105.0
                it.layoutY = 180.0
            }

            content.children.addAll(logo, title)

        }

    }

    private fun getHeroView(heroModel: HeroModel) = Pane().also {
        it.id = HERO_FIELD_ID

        val correctName = getCorrectHeroName(heroModel.name)
        if (userModel.gameStat.priorityHero.contains(correctName)) {
            it.isDisable = true
            usedHeroes[userModel.gameStat.priorityHero.indexOf(correctName)] = it
        }

        val icon = ImageView().also { img ->
            img.image = HeroImageRepository.findById(heroModel.icon)
            img.layoutX = 9.0
            img.layoutY = 9.0
        }

        viewNameHero(heroModel.name, it)

        it.setOnMouseClicked { event ->
            if (event.button == MouseButton.PRIMARY) {
                changePriorityHero(heroModel)

                it.isDisable = true
                usedHeroes[selectedPriorityHeroIndex].isDisable = false
                usedHeroes[selectedPriorityHeroIndex] = it
            }
        }
        it.children.add(icon)
    }

    private fun getPriorityView(heroModel: HeroModel?): Pane = Pane().also {
        it.id = HERO_PEEKED_ID
        it.layoutX = 14.0

        var image = DEFAULT_PHOTO

        if (heroModel != null) {
            HeroImageRepository.findById(heroModel.icon)?.let { i -> image = i }
        }

        val icon = ImageView().also { img ->
            img.image = image
            img.layoutX = 14.0
            img.layoutY = 9.0
        }

        val label = Label().also { l ->
            l.text = if (image == DEFAULT_PHOTO)
                langApplication.text.accounts.hero.random
            else heroModel?.name
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

                val index = userModel.gameStat.priorityHero.indexOf(getCorrectHeroName(heroModel!!.name))
                userModel.gameStat.priorityHero[index] = "default"
                usedHeroes[index].isDisable = false

                it.children.clear()
                it.children.addAll(getPriorityView(null).children)

                UserRepository.save(userModel)

                event.consume()
            }
            p.children.add(clearImg)
        }

        it.setOnMouseEntered { clear.isVisible = true && heroModel != null }
        it.setOnMouseExited { clear.isVisible = false }
        it.setOnMouseClicked { event ->
            changePriority(currentPriorityHeroes.indexOf(it))
            event.consume()
        }
        it.children.addAll(icon, label, clear)
    }

    private fun changePriority(index: Int) {
        if (currentPriorityHeroes[index] != selectedPriorityHero) {

            selectedPriorityHero.id = HERO_PEEKED_ID

            currentPriorityHeroes[index].id = HERO_SELECTED_PEEKED_ID
            selectedPriorityHeroIndex = index
            selectedPriorityHero = currentPriorityHeroes[index]
        }
    }

    private fun changePriorityHero(heroModel: HeroModel) {

        val prevOffsetX = selectedPriorityHero.layoutX
        val prevOffsetY = selectedPriorityHero.layoutY

        priority.children.remove(selectedPriorityHero)

        val priorityView = getPriorityView(heroModel).also {
            it.id = HERO_SELECTED_PEEKED_ID

            it.layoutX = prevOffsetX
            it.layoutY = prevOffsetY
        }

        selectedPriorityHero = priorityView
        currentPriorityHeroes[selectedPriorityHeroIndex] = priorityView
        priority.children.add(priorityView)

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

    private fun getCorrectHeroName(name: String) = name.trim()
        .replace(" ", "_")
        .replace("-", "_")
        .lowercase()

}