package com.project.steamfarm.ui.controller

import com.project.steamfarm.Runner
import com.project.steamfarm.model.ConfigModel
import com.project.steamfarm.model.LangModel
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import com.project.steamfarm.repository.Repository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.service.background.AuthBackground
import com.project.steamfarm.service.background.impl.DefaultAuthBackground
import com.project.steamfarm.ui.view.DefaultView
import com.project.steamfarm.ui.view.menu.MenuView
import com.project.steamfarm.ui.view.section.DefaultSectionView
import com.project.steamfarm.ui.view.section.StartSectionView
import javafx.application.Application
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.util.concurrent.CompletableFuture
import kotlin.system.exitProcess

const val NAME_APPLICATION = "Steam Farm"
private const val ICON_APPLICATION = "images/logo.png"
private const val WIDTH_APPLICATION = 800.0
private const val HEIGHT_APPLICATION = 600.0

private const val STYLESHEETS = "styles.css"

open class BaseController: Application() {

    companion object {

        val root: Pane = Pane().also {
            it.prefWidth = WIDTH_APPLICATION
            it.prefHeight = HEIGHT_APPLICATION

            it.children.addAll(
                getFooterRight(false),
                getFooterRight(true)
            )

            it.stylesheets.add(Runner::class.java.getResource(STYLESHEETS)!!.toString())
        }

        private fun getFooterRight(isClosed: Boolean): Pane = Pane().also {
            it.id = "footerRight"
            it.layoutX = if (isClosed) 740.0 else 700.0
            it.layoutY = 24.0

            val icon = ImageView().also { img ->
                img.id = if (isClosed) "close" else "collapse"
                img.fitWidth = 32.0
                img.fitHeight = 32.0
            }

            it.children.add(icon)

            it.setOnMouseClicked { _ ->
                if (isClosed) {
                    exitProcess(0)
                } else {
                    val stage = it.scene.window as Stage
                    stage.isIconified = true
                }
            }
        }

    }

    private val view: List<DefaultView> = mutableListOf(
        MenuView()
    )

    private var offsetX: Double = 0.0
    private var offsetY: Double = 0.0

    override fun start(primaryStage: Stage?) {

        primaryStage?.let {

            if (it.title != NAME_APPLICATION) {
                it.title = NAME_APPLICATION
            }

            if (it.style != StageStyle.TRANSPARENT) {
                it.initStyle(StageStyle.TRANSPARENT)
            }

            if (it.icons.isEmpty()) {
                it.icons.add(Image(Runner::class.java.getResourceAsStream(ICON_APPLICATION)))
            }

            it.scene = Scene(root, WIDTH_APPLICATION, HEIGHT_APPLICATION, Color.TRANSPARENT)
            it.scene.setOnMousePressed { event ->
                offsetX = primaryStage.x - event.screenX
                offsetY = primaryStage.y - event.screenY
            }
            it.scene.setOnMouseDragged { event ->
                it.scene.cursor = Cursor.MOVE
                primaryStage.x = event.screenX + offsetX
                primaryStage.y = event.screenY + offsetY
            }
            it.scene.setOnMouseReleased { _ -> it.scene.cursor = Cursor.DEFAULT }

            it.show()
            it.toFront()

            val startSection: DefaultSectionView = StartSectionView()
            startSection.initialize()

            view.forEach { v -> v.initialize() }

            val userRepository: Repository<UserModel> = UserRepository()
            val authBackground: AuthBackground = DefaultAuthBackground()
            userRepository.findAll().filter { u -> u.userType == UserType.WAIT_AUTH }
                .forEach { u -> authBackground.authenticate(u.username, u.password) }
        }
    }

}