package com.project.panel

import com.project.panel.model.ConfigModel
import com.project.panel.model.LangModel
import com.project.panel.repository.impl.LangRepository
import com.project.panel.ui.controller.MainController
import javafx.application.Application

lateinit var langApplication: LangModel

class Runner

fun main() {
    val configModel: ConfigModel = ConfigModel().fromFile()
    langApplication = LangRepository.findById(configModel.langApp) ?: LangModel()

    Application.launch(MainController::class.java)
}