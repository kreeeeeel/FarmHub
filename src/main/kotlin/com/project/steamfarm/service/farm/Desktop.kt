package com.project.steamfarm.service.farm

import com.project.steamfarm.data.WindowData
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.WM_KEYDOWN
import com.sun.jna.ptr.IntByReference
import org.sikuli.script.Region
import java.awt.Toolkit


val PATH_TO_IMG = "${System.getProperty("user.dir")}\\config\\ui"

const val VK_TAB = 0x09
const val VK_ENTER = 0x0D

const val WM_LBUTTONDOWN = 0x0201
const val WM_LBUTTONUP = 0x0202

const val DEFAULT_DURATION = 5.0

open class Desktop {

    protected val currentPid = IntByReference()
    private val rect = WinDef.RECT()

    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    fun getRegion(hWnd: HWND): Region {
        val offsetProperties = getOffsetProperties(hWnd)
        return Region(
            offsetProperties.offsetX,
            offsetProperties.offsetY,
            offsetProperties.width,
            offsetProperties.height
        )
    }

    fun getOffsetProperties(hWnd: HWND): WindowData {
        User32.INSTANCE.GetWindowRect(hWnd, rect)

        val screenWidth = rect.right - rect.left
        val screenHeight = rect.bottom - rect.top
        return WindowData(rect.left, rect.top, screenWidth, screenHeight)
    }

    fun postText(hWnd: HWND?, value: String) = value.toCharArray().forEach {
        typeChar(hWnd, it)
    }

    fun postKeyPress(hWnd: HWND?, key: Long) {
        User32.INSTANCE.PostMessage(hWnd, WM_KEYDOWN, WPARAM(key), LPARAM(0))
    }

    private fun typeChar(hWnd: HWND?, value: Char) {
        User32.INSTANCE.PostMessage(hWnd, WinUser.WH_MOUSE, WPARAM(0), null)
        User32.INSTANCE.PostMessage(hWnd, WinUser.WM_CHAR, WPARAM(value.toLong()), null)
        Thread.sleep(50)
    }

}
