package com.project.steamfarm

import com.project.steamfarm.model.ConfigModel
import com.project.steamfarm.model.LangModel
import com.project.steamfarm.repository.impl.LangRepository
import com.project.steamfarm.ui.controller.MainController
import javafx.application.Application

lateinit var langApplication: LangModel

class Runner

fun main() {
    val configModel: ConfigModel = ConfigModel().fromFile()
    langApplication = LangRepository.findById(configModel.langApp) ?: LangModel()

    Application.launch(MainController::class.java)
}