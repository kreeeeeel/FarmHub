package com.project.steamfarm.service.background.impl

import com.project.steamfarm.langApplication
import com.project.steamfarm.service.background.AuthBackground
import com.project.steamfarm.service.steam.ClientSteam
import com.project.steamfarm.service.steam.impl.DefaultClientSteam
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.view.notify.NotifyView
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class DefaultAuthBackground: AuthBackground {

    private val executor = Executors.newCachedThreadPool()
    private val clientSteam: ClientSteam = DefaultClientSteam()

    private val notifyView = NotifyView()

    override fun authenticate(username: String, password: String) {
        CompletableFuture.supplyAsync {
            val isLogin = clientSteam.authentication(username, password)

            Platform.runLater {

                val section = root.children.firstOrNull { it.id == "section" }
                if (section == null) return@runLater

                val scroll = (section as Pane).children.firstOrNull { it.id == "accounts" }
                if (scroll == null) return@runLater

                val content = (scroll as ScrollPane).content as AnchorPane
                val user = content.children.firstOrNull {
                    val user = it as Pane

                    val name = user.children.firstOrNull { u -> u.id == "userAuthName" }
                    return@firstOrNull name != null && (name as Label).text == username
                }

                val message = if (isLogin) langApplication.text.success.auth else langApplication.text.failure.auth
                val value = String.format("%s", message, username)

                if (isLogin) {
                    notifyView.success(value)
                } else notifyView.failure(value)

                println(user != null)
            }
        }
    }
}