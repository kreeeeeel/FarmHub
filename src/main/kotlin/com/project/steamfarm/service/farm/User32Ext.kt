package com.project.steamfarm.service.farm

import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND

@Suppress("FunctionName")
interface User32Ext: User32 {

    fun SetWindowTextA(hWnd: HWND?, lpString: String?): Boolean

    companion object {
        var INSTANCE: User32Ext = Native.load("user32", User32Ext::class.java)
    }

}