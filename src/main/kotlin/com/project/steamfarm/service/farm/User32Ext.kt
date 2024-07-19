package com.project.steamfarm.service.farm

import com.sun.jna.Native
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions

@Suppress("FunctionName")
interface User32Ext: StdCallLibrary {

    fun FindWindow(var1: String?, var2: String?): HWND?
    fun FindWindowEx(var1: HWND?, var2: HWND?, var3: String?, var4: String?): HWND?
    fun GetWindowThreadProcessId(var1: HWND?, var2: IntByReference?): Int
    fun GetWindowRect(var1: HWND?, var2: RECT?): Boolean
    fun SetForegroundWindow(var1: HWND?): Boolean
    fun SendMessage(var1: HWND?, var2: Int, var3: WPARAM?, var4: LPARAM?): LRESULT?
    fun PostMessage(var1: HWND?, var2: Int, var3: WPARAM?, var4: LPARAM?)

    fun SetWindowText(hWnd: HWND?, lpString: String): Boolean
    fun SetWindowPos(var1: HWND?, var2: HWND?, var3: Int, var4: Int, var5: Int, var6: Int, var7: Int): Boolean

    companion object {
        val INSTANCE: User32Ext = Native.load("user32", User32Ext::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }

}