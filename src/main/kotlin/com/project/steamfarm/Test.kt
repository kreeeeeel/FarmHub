package com.project.steamfarm

import com.project.steamfarm.model.ConfigModel
import com.project.steamfarm.model.LangModel
import com.project.steamfarm.repository.impl.LangRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.service.farm.Manager

fun main() {
    val configModel: ConfigModel = ConfigModel().fromFile()
    langApplication = LangRepository.findById(configModel.langApp) ?: LangModel()

    Manager.initUser(UserRepository.findAll().subList(0, 10).toMutableList())
    Manager.initGame(570)
    Manager.launchGame()
}