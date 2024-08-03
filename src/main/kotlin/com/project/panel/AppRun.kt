package com.project.panel

import com.project.panel.model.ConfigModel
import com.project.panel.model.LangModel
import com.project.panel.repository.impl.LangRepository
import com.project.panel.service.logger.LoggerService
import com.project.panel.ui.controller.MainController
import javafx.application.Application
import java.time.Instant

const val NAME_APPLICATION = "Steam FarmHub"
const val VERSION_APPLICATION = "1.0.0"

var langApplication = LangRepository.findById(ConfigModel().fromFile().langApp) ?: LangModel()
val startTime: Instant = Instant.now()

class AppRun

fun main() {
    LoggerService.getLogger().info("Launch application..")
    Application.launch(MainController::class.java)
}