package com.project.steamfarm

import com.project.steamfarm.model.ConfigModel
import com.project.steamfarm.model.LangModel
import com.project.steamfarm.repository.impl.LangRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.ui.controller.MainController
import javafx.application.Application
import java.io.BufferedReader
import java.io.InputStreamReader


class Test {

    private val steamPath: String = "\"H:\\Program Files (x86)\\Steam\\steam.exe\""
    private val command: String = "-w 380 -h 285 -sw -console -novid -low -nosound"
    private val gameId: Int = 570

    fun start() {

        //UserRepository.findAll().subList(0, 10).forEach {
            val process = ProcessBuilder("$steamPath").start()
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

        //}

    }

}

fun main() {
    Test().start()
}