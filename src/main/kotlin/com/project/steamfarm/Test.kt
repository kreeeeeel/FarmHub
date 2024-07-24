package com.project.steamfarm

import com.project.steamfarm.model.ConfigModel
import com.project.steamfarm.model.LangModel
import com.project.steamfarm.repository.impl.LangRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.service.farm.Manager
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