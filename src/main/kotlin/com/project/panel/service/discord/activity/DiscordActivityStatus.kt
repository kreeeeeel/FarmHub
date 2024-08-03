package com.project.panel.service.discord.activity

import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import java.time.Instant

class DiscordActivityStatus {

    private val params = CreateParams().apply {
        clientID = 1269143110324060253L
        flags = CreateParams.getDefaultFlags()
    }

    private val core = Core(params)

    fun initialize() {
        val activity = Activity().apply {
            timestamps().start = Instant.now()
            assets().largeImage = "1813617"
        }

        core.activityManager().updateActivity(activity)
        core.runCallbacks()
    }

}