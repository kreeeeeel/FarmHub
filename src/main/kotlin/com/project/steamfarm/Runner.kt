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
import java.awt.Button
import java.awt.Robot
import java.awt.event.InputEvent

lateinit var langApplication: LangModel

class Runner

fun main() {

    /*val configModel: ConfigModel = ConfigModel().fromFile()

    val userRepository: Repository<UserModel> = UserRepository()
    val authBackground: AuthBackground = DefaultAuthBackground()
    userRepository.findAll().filter { u -> u.userType == UserType.WAIT_AUTH }
        .forEach { u -> authBackground.authenticate(u.username, u.password) }

    val langRepository = LangRepository()
    langApplication = langRepository.findById(configModel.langApp) ?: LangModel()

    Application.launch(MainController::class.java)*/
    Thread.sleep(2000)
    val robot = Robot()
    while (true) {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        Thread.sleep(100)
    }

}