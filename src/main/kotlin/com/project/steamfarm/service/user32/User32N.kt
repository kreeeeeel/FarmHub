package com.project.steamfarm.service.user32

import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.win32.W32APIOptions

@Suppress("FunctionName")
interface User32N: User32 {
    fun SetWindowText(hWnd: WinDef.HWND, lpString: String): Boolean

    companion object {
        val INSTANCE: User32N = Native.load("user32", User32N::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }

}