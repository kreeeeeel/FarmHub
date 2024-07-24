package com.project.steamfarm.service.farm

import com.project.steamfarm.data.WindowData
import com.project.steamfarm.model.ConfigModel
import com.project.steamfarm.service.process.User32Ext
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.WM_KEYDOWN
import com.sun.jna.ptr.IntByReference
import kotlinx.coroutines.delay
import org.sikuli.script.Pattern
import org.sikuli.script.Region
import org.sikuli.script.support.RobotDesktop
import java.awt.event.InputEvent

val PATH_TO_IMG = "${System.getProperty("user.dir")}\\config\\ui"

const val VK_TAB = 0x09
const val VK_ENTER = 0x0D

const val MAX_ATTEMPTS = 60

open class Desktop {

    protected val currentPid = IntByReference()
    protected val configModel = ConfigModel().fromFile()

    private val robot = RobotDesktop()
    private val rect = RECT()

    fun click(hWnd: HWND, offsetX: Int, offsetY: Int) {
        User32Ext.INSTANCE.SetForegroundWindow(hWnd)

        robot.mouseMove(offsetX, offsetY)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    }

    fun getRegion(hWnd: HWND): Region {
        val offsetProperties = getOffsetProperties(hWnd)
        return Region(
            offsetProperties.offsetX,
            offsetProperties.offsetY,
            offsetProperties.width,
            offsetProperties.height
        )
    }

    fun isCurrentPage(hwnd: HWND, pattern: Pattern, duration: Double? = null): Boolean = try {
        User32Ext.INSTANCE.SetForegroundWindow(hwnd)
        if (duration == null) getRegion(hwnd).find(pattern)
        else getRegion(hwnd).wait(pattern, duration)
        true
    } catch (ignored: Exception) {false}

    fun getOffsetProperties(hWnd: HWND): WindowData {
        User32Ext.INSTANCE.GetWindowRect(hWnd, rect)

        val screenWidth = rect.right - rect.left
        val screenHeight = rect.bottom - rect.top
        return WindowData(rect.left, rect.top, screenWidth, screenHeight)
    }

    fun postText(hWnd: HWND?, value: String) = value.toCharArray().forEach {
        typeChar(hWnd, it)
    }

    suspend fun postKeyPress(hWnd: HWND?, key: Long) {
        User32Ext.INSTANCE.PostMessage(hWnd, WM_KEYDOWN, WPARAM(key), LPARAM(0))
        delay(150)
    }

    @Suppress("DEPRECATION")
    fun typeChar(hWnd: HWND?, value: Char) {
        User32Ext.INSTANCE.SendMessage(hWnd, WinUser.WM_CHAR, WPARAM(value.toLong()), null)
    }

}
