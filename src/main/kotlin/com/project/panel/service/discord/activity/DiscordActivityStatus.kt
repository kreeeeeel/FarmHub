package com.project.panel.service.discord.activity

import com.project.panel.startTime
import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import java.util.concurrent.CompletableFuture

object DiscordActivityStatus {

    private val params = CreateParams().apply {
        clientID = 1269143110324060253L
        flags = CreateParams.getDefaultFlags()
    }

    private var currentCore: Core? = null

    fun initialize() = CompletableFuture.supplyAsync {
        val core = Core(params)
        val activity = Activity().apply {
            timestamps().start = startTime
            assets().largeImage = "1813617"
        }

        core.activityManager().updateActivity(activity)
        core.runCallbacks()
        currentCore = core
    }!!

    fun shutdown() = CompletableFuture.supplyAsync  {
        currentCore?.activityManager()?.clearActivity()
        currentCore?.close()
    }!!

}