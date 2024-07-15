package com.project.steamfarm.service.user32

import com.sun.jna.Native
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser.INPUT
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions

@Suppress("FunctionName")
interface User32: StdCallLibrary {


    fun FindWindow(lpClassName: String?, lpWindowName: String?): WinDef.HWND?
    fun SetForegroundWindow(hwnd: WinDef.HWND): Boolean
    fun GetWindowThreadProcessId(hwnd: WinDef.HWND, lpdwProcessId: IntByReference): Int
    fun GetWindowRect(hwnd: WinDef.HWND, rect: WinDef.RECT): Boolean
    fun SendInput(var1: WinDef.DWORD?, var2: Array<INPUT?>?, var3: Int): WinDef.DWORD?

    companion object {
        val INSTANCE: User32 = Native.load("user32", User32::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }

}