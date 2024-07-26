package com.project.panel.ui.controller

import com.project.panel.Runner
import com.project.panel.ui.view.menu.MenuView
import com.project.panel.ui.view.section.SECTION_CLOSE_ID
import com.project.panel.ui.view.section.SECTION_ID
import com.project.panel.ui.view.section.SECTION_NAME_ID
import com.project.panel.ui.view.section.StartSectionView
import com.project.panel.ui.view.modal.WINDOW_ID
import javafx.application.Application
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlin.system.exitProcess

const val NAME_APPLICATION = "Steam FarmHub"
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

    private val menuView = MenuView()
    private val startSection = StartSectionView()

    private var offsetX: Double? = 0.0
    private var offsetY: Double? = 0.0

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
                if (event.y <= primaryStage.height / 10) {
                    offsetX = primaryStage.x - event.screenX
                    offsetY = primaryStage.y - event.screenY
                }
            }
            it.scene.setOnMouseDragged { event ->
                if (offsetX != null && offsetY != null) {
                    it.scene.cursor = Cursor.MOVE
                    primaryStage.x = event.screenX + offsetX!!
                    primaryStage.y = event.screenY + offsetY!!
                }
            }
            it.scene.setOnMouseReleased { _ ->
                offsetX = null
                offsetY = null
                it.scene.cursor = Cursor.DEFAULT
            }
            it.scene.setOnKeyReleased { event ->

                if (!startSection.isStartSection() && event.code == KeyCode.ESCAPE) {
                    root.children.removeIf { node ->
                        node.id == SECTION_ID || node.id == SECTION_NAME_ID || node.id == SECTION_CLOSE_ID || node.id == WINDOW_ID
                    }

                    startSection.initialize()
                    menuView.enablePrevActivePoint()
                }

            }

            it.show()
            it.toFront()

            startSection.initialize()
            menuView.initialize()
        }
    }

}