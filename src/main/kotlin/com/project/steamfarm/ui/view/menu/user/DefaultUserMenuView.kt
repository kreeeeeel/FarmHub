package com.project.steamfarm.ui.view.menu.user

import com.project.steamfarm.ui.controller.BaseController.Companion.root
import javafx.animation.ScaleTransition
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.util.Duration

const val USER_EDIT_MENU_ID = "editMenu"
const val USER_MENU_VIEW_ID = "userMenuView"

const val USER_EDIT_BUTTON_MENU_ID = "editMenuButton"
const val USER_EDIT_MENU_TEXT_ID = "editMenuText"

abstract class DefaultUserMenuView {

    protected val menu: Pane = Pane().also {
        it.setOnMouseExited { closeMenu() }
        root.scene.addEventFilter(MouseEvent.MOUSE_CLICKED) { event ->
            if (!it.boundsInParent.contains(event.x, event.y)) { closeMenu() }
        }
    }

    abstract fun openMenu(): Pane

    fun closeMenu() = Platform.runLater {
        if (root.children.contains(menu)) {
            root.children.remove(menu)
        }
    }

    fun animateScaleMenu(menu: Pane) = ScaleTransition(Duration.millis(150.0), menu).also {
        menu.scaleX = 0.1
        menu.scaleY = 0.1

        it.fromX = 0.1
        it.fromY = 0.1
        it.toX = 1.0
        it.toY = 1.0

        it.play()
    }

    fun getButtonMenu(iconId: String, value: String) = Pane().also {
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

}