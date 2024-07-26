package com.project.panel.service.farm.steam

import com.project.panel.service.farm.Desktop
import com.project.panel.service.farm.MAX_ATTEMPTS
import com.project.panel.service.farm.STEAM_PATH
import com.project.panel.service.process.User32Ext
import com.project.panel.service.logger.LoggerService
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.WPARAM
import com.sun.jna.platform.win32.WinUser.WM_CLOSE
import kotlinx.coroutines.delay
import org.sikuli.script.Pattern
import java.io.File

private val CLOUD_OUT_DATE_PATH = "$STEAM_PATH\\cloud_out_of_date.png"

private const val OFFSET_X_CONFLICT_CLOUD = 45
private const val OFFSET_Y_CONFLICT_CLOUD = 220

private const val OFFSET_X_CONFLICT_SAVE = 500
private const val OFFSET_Y_CONFLICT_SAVE = 510

private const val OFFSET_X_CONFLICT_CLOUD_DATE = 275
private const val OFFSET_Y_CONFLICT_CLOUD_DATE = 350

abstract class GameDesktop: Desktop() {

    private val patternCloudOutDate = Pattern(CLOUD_OUT_DATE_PATH)

    init {
        if (!File(CLOUD_OUT_DATE_PATH).exists()) throw NullPointerException("$CLOUD_OUT_DATE_PATH is not found.")
    }

    abstract fun getCommand(): List<String>
    abstract suspend fun setName(hWnd: HWND, username: String)
    abstract suspend fun getGameHwnd(): HWND
    abstract suspend fun setReadyGame(hWnd: HWND)
    abstract suspend fun sendInvite(hWnd: HWND, steamId: String)
    abstract suspend fun acceptInvite(hWnd: HWND): Boolean

    suspend fun closeSupport() {
        LoggerService.getLogger().info("Checking for 'Support Message' window")
        var hWnd: HWND? = User32Ext.INSTANCE.FindWindow(null, "Support Message")
        var attempts = 0
        while (hWnd == null && attempts++ < MAX_ATTEMPTS) {
            hWnd = User32Ext.INSTANCE.FindWindow(null, "Support Message")
            delay(1000)
        }

        if (hWnd != null) {
            LoggerService.getLogger().info("Closing the 'Support Message' window")
            User32Ext.INSTANCE.PostMessage(hWnd, WM_CLOSE, WPARAM(0), WinDef.LPARAM(0))
        }
    }

    suspend fun closeCloudConflict() {
        LoggerService.getLogger().info("Checking for 'Conflict' window")
        var hWnd: HWND? = User32Ext.INSTANCE.FindWindow(null, "Steam Dialog")
        var attempt = 0

        while (hWnd == null && attempt++ < MAX_ATTEMPTS) {
            hWnd = User32Ext.INSTANCE.FindWindow(null, "Steam Dialog")
            delay(1000)
        }

        if (hWnd != null) {
            if (isCurrentPage(hWnd, patternCloudOutDate, 5.0)) {
                closeCloudOutOfDate(hWnd)
            } else closeCloud(hWnd)

            LoggerService.getLogger().info("Closing the 'Conflict' window")
            val steamHwnd = User32Ext.INSTANCE.FindWindow(null, "Steam")
            User32Ext.INSTANCE.PostMessage(steamHwnd, WM_CLOSE, WPARAM(0), WinDef.LPARAM(0))
        }
    }

    private fun closeCloudOutOfDate(hWnd: HWND) {
        LoggerService.getLogger().info("There is a date conflict")
        val offsetProperties = getOffsetProperties(hWnd)
        val cloudOffsetX = offsetProperties.offsetX + OFFSET_X_CONFLICT_CLOUD_DATE
        val cloudOffsetY = offsetProperties.offsetY + OFFSET_Y_CONFLICT_CLOUD_DATE
        click(hWnd, cloudOffsetX, cloudOffsetY)
    }

    private fun closeCloud(hWnd: HWND) {
        LoggerService.getLogger().info("There is a conflict on the PC")
        val offsetProperties = getOffsetProperties(hWnd)
        val cloudOffsetX = offsetProperties.offsetX + OFFSET_X_CONFLICT_CLOUD
        val cloudOffsetY = offsetProperties.offsetY + OFFSET_Y_CONFLICT_CLOUD
        click(hWnd, cloudOffsetX, cloudOffsetY)

        val saveOffsetX = offsetProperties.offsetX + OFFSET_X_CONFLICT_SAVE
        val saveOffsetY = offsetProperties.offsetY + OFFSET_Y_CONFLICT_SAVE
        click(hWnd, saveOffsetX, saveOffsetY)
    }
}
