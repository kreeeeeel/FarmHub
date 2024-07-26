package com.project.panel

import com.project.panel.model.ConfigModel
import com.project.panel.model.LangModel
import com.project.panel.repository.impl.LangRepository
import com.project.panel.repository.impl.UserRepository
import com.project.panel.service.farm.Manager
import kotlinx.coroutines.delay

suspend fun main() {
    val configModel: ConfigModel = ConfigModel().fromFile()
    langApplication = LangRepository.findById(configModel.langApp) ?: LangModel()

    Manager.initGame(570)
    //Manager.testByKrel(UserRepository.findAll().subList(0, 3))
    Manager.initRandomLobby(UserRepository.findAll().subList(0, 10).toSet())
    Manager.start()
    delay(1000)
    Manager.inviteToLobby()

}