package com.project.steamfarm.service.farm.steam.impl

import com.project.steamfarm.model.HEIGHT_APP
import com.project.steamfarm.model.WIDTH_APP
import com.project.steamfarm.service.farm.steam.GameDesktop
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import java.io.File
import java.io.FileReader
import java.io.FileWriter

private const val DOTA_GAME_NAME = "Dota 2"
private const val NAME_CONFIG = "dota2_low.cfg"

class DotaGameDesktop: GameDesktop() {

    private val cfg = File("${System.getProperty("user.dir")}\\config\\$NAME_CONFIG")
    private var counter = 120

    init {
        if (!cfg.exists()) throw NullPointerException("Dota 2 cfg is not found!")
        if (config.dota2Path == null) throw NullPointerException("Dota 2 not found!")
    }

    override fun setConfig() = FileWriter("${config.dota2Path}\\game\\dota\\cfg\\$NAME_CONFIG").use {
        writer -> writer.write(FileReader(cfg).use { reader -> reader.readText() })
    }

    override fun getCommand(): List<String> = listOf(
        "-applaunch", "570", "-language", "english", "-w", "$WIDTH_APP", "-h", "$HEIGHT_APP", "-prewarm", "-noaafonts",
        "-nod3d9ex", "-novid", "-console", "-freq", "60", "-threads", "3", "-high", "+rate", "128000",
        "-cl_cmdrate", "128", "+cl_interp", "0", "+cl_interp_ratio", "+fps_max", "0", "+dota_embers" ,"0",
        "+exec", NAME_CONFIG, "-nosound"
    )

    override fun gameLaunched(): HWND? {
        var hWnd = User32.INSTANCE.FindWindow(null, DOTA_GAME_NAME)
        while (counter-- != 0 && hWnd == null) {
            Thread.sleep(1000)
            hWnd = User32.INSTANCE.FindWindow(null, DOTA_GAME_NAME)
        }
        return hWnd
    }

}