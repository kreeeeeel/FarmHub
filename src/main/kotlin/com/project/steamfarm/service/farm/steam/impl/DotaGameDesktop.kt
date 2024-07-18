package com.project.steamfarm.service.farm.steam.impl

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.HEIGHT_APP
import com.project.steamfarm.model.WIDTH_APP
import com.project.steamfarm.service.farm.User32Ext
import com.project.steamfarm.service.farm.steam.GameDesktop
import com.project.steamfarm.service.logger.LoggerService
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileReader
import java.io.FileWriter

private const val DOTA_GAME_NAME = "Dota 2"
private const val NAME_CONFIG = "dota2_low.cfg"

class DotaGameDesktop: GameDesktop() {

    private val cfg = File("${System.getProperty("user.dir")}\\config\\$NAME_CONFIG")

    init {
        if (!cfg.exists()) throw NullPointerException("Dota 2 cfg is not found!")
        if (configModel.dota2Path == null) throw NullPointerException("Dota 2 not found!")
    }

    override fun setConfig() = FileWriter("${configModel.dota2Path}\\game\\dota\\cfg\\$NAME_CONFIG").use {
        writer -> writer.write(FileReader(cfg).use { reader -> reader.readText() })
    }

    override fun getCommand(): List<String> = listOf(
        "-applaunch", "570", "-language", "english", "-w", "$WIDTH_APP", "-h", "$HEIGHT_APP", "-novid", "-prewarm",
        "-noaafonts", "-nod3d9ex", "-console", "+exec", "$NAME_CONFIG.cfg", "-nosound", "-high",
        "-autoconfig_level", "0", "-maxtextureres", "2"
    )

    override suspend fun setName(hWnd: HWND, username: String) {
        val name = String.format(langApplication.text.farm.service.dota, username)
        LoggerService.getLogger().info("Changing Dota2 window name to $name")
        User32Ext.INSTANCE.SetWindowText(hWnd, name)
    }

    override suspend fun getGameHwnd(): HWND {
        LoggerService.getLogger().info("Search dota2 window")
        var hWnd = User32.INSTANCE.FindWindow(null, DOTA_GAME_NAME)
        while (hWnd == null) {
            System.gc()
            delay(1000)
            hWnd = User32.INSTANCE.FindWindow(null, DOTA_GAME_NAME)
        }
        System.gc()
        return hWnd
    }

}