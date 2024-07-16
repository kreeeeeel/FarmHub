package com.project.steamfarm.service.farm.steam.impl

import com.project.steamfarm.service.farm.steam.GameDesktop
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND

private const val DOTA_GAME_NAME = "Dota 2"

class DotaGameDesktop: GameDesktop() {

    private var counter = 120

    override fun gameLaunched(): HWND? {
        var hWnd = User32.INSTANCE.FindWindow(null, DOTA_GAME_NAME)
        while (counter-- != 0 && hWnd == null) {
            Thread.sleep(1000)
            hWnd = User32.INSTANCE.FindWindow(null, DOTA_GAME_NAME)
        }
        return hWnd
    }

}