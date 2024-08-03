package com.project.panel.ui.view.block.settings

import com.project.panel.langApplication
import com.project.panel.service.discord.activity.DiscordActivityStatus
import com.project.panel.ui.view.component.ToggleComponent
import javafx.scene.control.Label
import javafx.scene.image.ImageView

private const val PREF_HEIGHT = 40.0
const val DISCORD_ID = "steam"

class DiscordBlockView: SettingsBlockView(DISCORD_ID) {

    private val icon = ImageView().apply {
        id = "discord"
        layoutX = 10.0
        layoutY = 4.0
    }

    private val title = Label(langApplication.text.settings.showGameActivity).apply {
        layoutX = 52.0
        layoutY = 10.0
    }

    private val toggle = ToggleComponent()
    private val toggleView = toggle.getToggle(configModel.discordActivity)

    override fun setPrefHeight(): Double {

        block.children.addAll(icon, title, toggleView)
        block.prefHeight = PREF_HEIGHT

        toggleView.setOnMouseClicked { event ->
            configModel.discordActivity = !configModel.discordActivity
            configModel.save()
            toggle.action()

            if (configModel.discordActivity) DiscordActivityStatus.initialize()
            else DiscordActivityStatus.shutdown()

            event.consume()
        }
        return PREF_HEIGHT
    }

    override fun refreshLang() {
        title.text = langApplication.text.settings.showGameActivity
    }

}