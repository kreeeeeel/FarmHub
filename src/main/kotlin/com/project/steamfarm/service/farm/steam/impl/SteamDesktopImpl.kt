package com.project.steamfarm.service.farm.steam.impl

import com.project.steamfarm.service.farm.PATH_TO_IMG
import com.project.steamfarm.service.farm.User32Ext
import com.project.steamfarm.service.farm.VK_ENTER
import com.project.steamfarm.service.farm.VK_TAB
import com.project.steamfarm.service.farm.steam.STEAM_SIGN_NAME
import com.project.steamfarm.service.farm.steam.SteamDesktop
import com.project.steamfarm.service.steam.GuardSteam
import com.project.steamfarm.service.steam.impl.DefaultGuardSteam
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.WPARAM
import com.sun.jna.platform.win32.WinUser.WH_MOUSE
import com.sun.jna.platform.win32.WinUser.WM_CLOSE
import org.sikuli.script.Pattern
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val STEAM_PATH = "$PATH_TO_IMG\\steam"

private val LOGO_PATH = "$STEAM_PATH\\logo.png"
private val GUARD_PATH = "$STEAM_PATH\\guard.png"

private val CLOUD_OUT_DATE_PATH = "$STEAM_PATH\\cloud_out_of_date.png"

private const val DURATION_WAIT_GUARD = 15.0
private const val DURATION_WAIT_SUPPORT_MESSAGE = 15.0
private const val DURATION_WAIT_CONFLICT = 10.0

private const val OFFSET_X_CONFLICT_CLOUD = 45
private const val OFFSET_Y_CONFLICT_CLOUD = 220

private const val OFFSET_X_CONFLICT_SAVE = 500
private const val OFFSET_Y_CONFLICT_SAVE = 510

private const val OFFSET_X_CONFLICT_CLOUD_DATE = 275
private const val OFFSET_Y_CONFLICT_CLOUD_DATE = 350

class SteamDesktopImpl: SteamDesktop() {

    private val patternLogo = Pattern(LOGO_PATH)
    private val patternGuard = Pattern(GUARD_PATH)
    private val patternCloudOutDate = Pattern(CLOUD_OUT_DATE_PATH)

    private val guardSteam: GuardSteam = DefaultGuardSteam()
    private val dotaGameDesktop = DotaGameDesktop()

    init {
        if (!File(GUARD_PATH).exists()) throw NullPointerException("$GUARD_PATH is not found.")
        if (!File(LOGO_PATH).exists()) throw NullPointerException("$LOGO_PATH is not found.")
        if (!File(CLOUD_OUT_DATE_PATH).exists()) throw NullPointerException("$CLOUD_OUT_DATE_PATH is not found.")

        dotaGameDesktop.setConfig()
    }

    override fun start(ipcName: String, gameId: Int) {

        val processBuilder = ProcessBuilder()

        val random = (1000..50000).random().toString()
        processBuilder.environment()["VPROJECT"] = random

        val command = mutableListOf(
            configModel.steamExecutor, "-login", "-silent", "-nofriendsui", "-vgui", "-noreactlogin", "-noverifyfiles",
            "-nobootstrapupdate", "-skipinitialbootstrap", "-norepairfiles", "-overridepackageurl", "-disable-winh264",
            "-language", "english", "-master_ipc_name_override", "$ipcName$random"
        ).apply {
            addAll(dotaGameDesktop.getCommand())
            add("-allowmultiple")
        }

        processBuilder.command(command).start()
    }

    override fun signIn(username: String, password: String) {

        var hWnd: HWND? = null
        while (hWnd == null) {
            hWnd = User32Ext.INSTANCE.FindWindow(null, STEAM_SIGN_NAME)
        }

        while (!isCurrentPage(hWnd, patternLogo, 1.0)) { continue }

        /* Нужно обязательно кликакать на эту хуйню чтобы ввод работа */
        val cefBrowserHwnd = User32Ext.INSTANCE.FindWindowEx(hWnd, null, "CefBrowserWindow", null)
        User32Ext.INSTANCE.PostMessage(cefBrowserHwnd, WH_MOUSE, WPARAM(0), null)

        val widgetHwnd = User32Ext.INSTANCE.FindWindowEx(cefBrowserHwnd, null, "Chrome_WidgetWin_0", null)

        postText(widgetHwnd, username)
        postKeyPress(widgetHwnd, VK_TAB.toLong())

        postText(widgetHwnd, password)
        postKeyPress(widgetHwnd, VK_ENTER.toLong())
    }

    override fun guard(sharedSecret: String): Boolean {
        try {

            val hWnd = User32Ext.INSTANCE.FindWindow(null, STEAM_SIGN_NAME) ?: return false
            if (!isCurrentPage(hWnd, patternGuard, DURATION_WAIT_GUARD)) return false

            val cefBrowserHwnd = User32Ext.INSTANCE.FindWindowEx(hWnd, null, "CefBrowserWindow", null)
            User32Ext.INSTANCE.PostMessage(cefBrowserHwnd, WH_MOUSE, WPARAM(0), null)

            val widgetHwnd = User32Ext.INSTANCE.FindWindowEx(cefBrowserHwnd, null, "Chrome_WidgetWin_0", null)
            postKeyPress(widgetHwnd, VK_TAB.toLong())

            val code = guardSteam.getCode(sharedSecret)
            postText(widgetHwnd, code)

            return true
        } catch (ignored: Exception) { return false }
    }

    override fun closeSupportMessage() {
        val executorService = Executors.newSingleThreadExecutor()
        executorService.submit {
            var count = DURATION_WAIT_SUPPORT_MESSAGE
            var hWnd: HWND? = null

            while (count-- > 0 && hWnd == null) {
                hWnd = User32Ext.INSTANCE.FindWindow(null, "Support Message")
                TimeUnit.SECONDS.sleep(1)
            }

            if (hWnd != null) {
                User32Ext.INSTANCE.PostMessage(hWnd, WM_CLOSE, WPARAM(0), WinDef.LPARAM(0))
            }
        }
        executorService.shutdown()
    }

    override fun closeConflictDialog()  {
        val executorService = Executors.newSingleThreadExecutor()
        executorService.submit {
            var count = DURATION_WAIT_CONFLICT
            var hWnd: HWND? = null

            while (count-- > 0 && hWnd == null) {
                hWnd = User32Ext.INSTANCE.FindWindow(null, "Steam Dialog")
                TimeUnit.SECONDS.sleep(1)
            }

            if (hWnd != null) {
                TimeUnit.SECONDS.sleep(1)
                if (isCurrentPage(hWnd, patternCloudOutDate, 5.0)) {
                    closeCloudOutOfDate(hWnd)
                } else closeCloud(hWnd)

                val steamHwnd = User32Ext.INSTANCE.FindWindow(null, "Steam")
                User32Ext.INSTANCE.PostMessage(steamHwnd, WM_CLOSE, WPARAM(0), WinDef.LPARAM(0))
            }
        }
        executorService.shutdown()
    }

    private fun closeCloudOutOfDate(hWnd: HWND) {
        val offsetProperties = getOffsetProperties(hWnd)
        val cloudOffsetX = offsetProperties.offsetX + OFFSET_X_CONFLICT_CLOUD_DATE
        val cloudOffsetY = offsetProperties.offsetY + OFFSET_Y_CONFLICT_CLOUD_DATE
        click(hWnd, cloudOffsetX, cloudOffsetY)
    }

    private fun closeCloud(hWnd: HWND) {
        val offsetProperties = getOffsetProperties(hWnd)
        val cloudOffsetX = offsetProperties.offsetX + OFFSET_X_CONFLICT_CLOUD
        val cloudOffsetY = offsetProperties.offsetY + OFFSET_Y_CONFLICT_CLOUD
        click(hWnd, cloudOffsetX, cloudOffsetY)

        val saveOffsetX = offsetProperties.offsetX + OFFSET_X_CONFLICT_SAVE
        val saveOffsetY = offsetProperties.offsetY + OFFSET_Y_CONFLICT_SAVE
        click(hWnd, saveOffsetX, saveOffsetY)
    }

}