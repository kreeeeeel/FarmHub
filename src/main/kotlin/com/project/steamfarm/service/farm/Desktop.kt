package com.project.steamfarm.service.farm

import com.project.steamfarm.data.WindowData
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.ptr.IntByReference
import org.sikuli.script.Region
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

val PATH_TO_IMG = "${System.getProperty("user.dir")}\\config\\ui"

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