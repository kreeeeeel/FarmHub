package com.project.steamfarm.service.farm.steam

import com.project.steamfarm.service.farm.Desktop
import com.sun.jna.platform.win32.WinDef.HWND

abstract class GameDesktop: Desktop() {
    
    abstract fun gameLaunched(): HWND?
    
}