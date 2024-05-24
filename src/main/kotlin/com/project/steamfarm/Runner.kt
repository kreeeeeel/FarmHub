package com.project.steamfarm

import com.project.steamfarm.model.ConfigModel
import com.project.steamfarm.model.LangModel
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import com.project.steamfarm.repository.Repository
import com.project.steamfarm.repository.impl.LangRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.service.background.AuthBackground
import com.project.steamfarm.service.background.impl.DefaultAuthBackground
import com.project.steamfarm.ui.controller.MainController
import javafx.application.Application
import java.util.concurrent.CompletableFuture

lateinit var langApplication: LangModel

class Runner

fun main() {

    val configModel: ConfigModel = ConfigModel().fromFile()

    val langRepository = LangRepository()
    langApplication = langRepository.findById(configModel.langApp) ?: LangModel()

    Application.launch(MainController::class.java)
}