package com.project.steamfarm

import com.project.steamfarm.model.ConfigModel
import com.project.steamfarm.model.LangModel
import com.project.steamfarm.repository.impl.LangRepository
import com.project.steamfarm.repository.impl.MaFileRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.service.steam.impl.DefaultGuardSteam
import com.project.steamfarm.ui.controller.MainController
import javafx.application.Application
import java.io.BufferedReader
import java.io.InputStreamReader


class Test {

    private val steamPath: String = "\"C:\\Program Files (x86)\\steam\\steam.exe\""
    private val command: String = "-w 380 -h 285 -sw -console -novid -low -nosound"
    private val gameId: Int = 570

    private val guard = DefaultGuardSteam()

    fun start() {

        UserRepository.findAll()[0].let {

            val data = MaFileRepository.findById(it.username)

            //val command = listOf(steamPath, "-applaunch", "570")
            val command = listOf(steamPath, "set_steam_guard_code", guard.getCode(data!!.sharedSecret), it.username, it.password)

            val process = ProcessBuilder(command).start()
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            val er = BufferedReader(InputStreamReader(process.errorStream))

            var s: String
            while ((`in`.readLine().also { s = it }) != null) {
                println(s)
            }
            while ((er.readLine().also { s = it }) != null) {
                println(s)
            }

            val status = process.waitFor()
            println("Exited with status: $status")

        }

    }

}

fun main() {
    Test().start()
}