package com.project.steamfarm.ui.view.menu.account

import com.project.steamfarm.langApplication
import com.project.steamfarm.ui.view.section.CS_NAME
import com.project.steamfarm.ui.view.section.DOTA_NAME
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

const val USER_EDIT_MENU_ID = "editMenu"
const val USER_EDIT_BUTTON_MENU_ID = "editMenuButton"
const val USER_EDIT_MENU_TEXT_ID = "editMenuText"

abstract class DefaultAccountMenuView {

    abstract fun getMenu(): Pane

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

    fun getStatusGame(isDotaEnabled: Boolean, isCsEnabled: Boolean): String {
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

}