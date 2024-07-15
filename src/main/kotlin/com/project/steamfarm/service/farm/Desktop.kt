package com.project.steamfarm.service.farm

import com.project.steamfarm.data.WindowData
import com.project.steamfarm.service.user32.User32N
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.ptr.IntByReference
import org.sikuli.script.Region
import org.sikuli.script.support.RobotDesktop
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.InputEvent
import java.awt.event.KeyEvent

val PATH_TO_IMG = "${System.getProperty("user.dir")}\\config\\ui"

const val DEFAULT_DURATION = 5.0

open class Desktop {

    protected val currentPid = IntByReference()
    private val rect = WinDef.RECT()
    private val robot = RobotDesktop()

    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    fun click(offsetX: Int, offsetY: Int) {
        robot.mouseMove(offsetX, offsetY)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    }

    fun input(value: String) = try {

        val originalClipboard = getClipboardText()
        clipboard.setContents(StringSelection(value), null)

        robot.keyPress(KeyEvent.VK_CONTROL)
        robot.keyPress(KeyEvent.VK_A)
        robot.keyRelease(KeyEvent.VK_A)
        robot.keyRelease(KeyEvent.VK_CONTROL)

        robot.keyPress(KeyEvent.VK_BACK_SPACE)
        robot.keyRelease(KeyEvent.VK_BACK_SPACE)

        robot.keyPress(KeyEvent.VK_CONTROL)
        robot.keyPress(KeyEvent.VK_V)
        robot.keyRelease(KeyEvent.VK_CONTROL)
        robot.keyRelease(KeyEvent.VK_V)

        clipboard.setContents(StringSelection(originalClipboard), null)
    } catch (ignored: Exception) {}

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
        User32N.INSTANCE.GetWindowRect(hWnd, rect)

        val screenWidth = rect.right - rect.left
        val screenHeight = rect.bottom - rect.top
        return WindowData(rect.left, rect.top, screenWidth, screenHeight)
    }

    private fun getClipboardText(): String? {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val data = clipboard.getContents(null)
        return if (data != null && data.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            data.getTransferData(DataFlavor.stringFlavor) as String
        } else {
            null
        }
    }

}