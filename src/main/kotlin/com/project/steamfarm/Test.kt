package com.project.steamfarm

import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.service.steam.impl.DefaultGuardSteam
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Suppress("unused")
class Test {

    private val steamPath: String = "\"H:\\Program Files (x86)\\Steam\\steam.exe\""
    private val command: String = "-w 380 -h 285 -sw -console -novid -low -nosound"
    private val gameId: Int = 570

    private val guard = DefaultGuardSteam()

    fun start() {

        UserRepository.findAll()[1].let {

            val text = "login ${it.steam.accountName} ${it.steam.password} ${guard.getCode(it.steam.sharedSecret)}"
            val stringSelection = StringSelection(text)
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(stringSelection, null)

            //val command = listOf(steamPath, "-applaunch", "570")
            /*val command = listOf(
                steamPath,
                "-nofriendsui",
                "-vgui",
                "-noreactlogin",
                "-noverifyfiles",
                "-nobootstrapupdate",
                "-skipinitialbootstrap",
                "-norepairfiles",
                "-overridepackageurl",
                "-disable-winh264",
                "-language",
                "english"
            )

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
            println("Exited with status: $status")*/

        }

    }

}

fun main() {
    Test().start()
}