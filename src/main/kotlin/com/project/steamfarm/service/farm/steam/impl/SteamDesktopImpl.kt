package com.project.steamfarm.service.farm.steam.impl

import com.project.steamfarm.service.farm.PATH_TO_IMG
import com.project.steamfarm.service.farm.User32Ext
import com.project.steamfarm.service.farm.VK_ENTER
import com.project.steamfarm.service.farm.VK_TAB
import com.project.steamfarm.service.farm.steam.GameDesktop
import com.project.steamfarm.service.farm.steam.STEAM_SIGN_NAME
import com.project.steamfarm.service.farm.steam.SteamDesktop
import com.project.steamfarm.service.logger.LoggerService
import com.project.steamfarm.service.steam.GuardSteam
import com.project.steamfarm.service.steam.impl.DefaultGuardSteam
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.WPARAM
import com.sun.jna.platform.win32.WinUser.WH_MOUSE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.sikuli.script.Pattern
import java.io.File

private val STEAM_PATH = "$PATH_TO_IMG\\steam"

private val LOGO_PATH = "$STEAM_PATH\\logo.png"
private val GUARD_PATH = "$STEAM_PATH\\guard.png"

private const val DURATION_WAIT_GUARD = 5.0

class SteamDesktopImpl(
    private val gameDesktop: GameDesktop
): SteamDesktop() {

    private val patternLogo = Pattern(LOGO_PATH)
    private val patternGuard = Pattern(GUARD_PATH)

    private val guardSteam: GuardSteam = DefaultGuardSteam()

    init {
        if (!File(GUARD_PATH).exists()) throw NullPointerException("$GUARD_PATH is not found.")
        if (!File(LOGO_PATH).exists()) throw NullPointerException("$LOGO_PATH is not found.")

        gameDesktop.setConfig()
    }

    override suspend fun start(ipcName: String) {

        val processBuilder = ProcessBuilder()

        val random = (1000..50000).random().toString()
        processBuilder.environment()["VPROJECT"] = random

        val command = mutableListOf(
            configModel.steamExecutor, "-login", "-silent", "-nofriendsui", "-vgui", "-noreactlogin", "-noverifyfiles",
            "-nobootstrapupdate", "-skipinitialbootstrap", "-norepairfiles", "-overridepackageurl", "-disable-winh264",
            "-language", "english", "-master_ipc_name_override", "$ipcName$random"
        ).apply {
            addAll(gameDesktop.getCommand())
            addAll(listOf("-allowmultiple", "-swapcores", "-noqueuedload"))
        }

        withContext(Dispatchers.IO) {
            LoggerService.getLogger().info("Start launch Steam.")

            val params = StringBuilder().also { command.forEach { c -> it.append(c).append(" ") } }.trim()
            LoggerService.getLogger().debug("Steam params: $params")

            processBuilder.command(command).start()
        }
    }

    override suspend fun signIn(username: String, password: String) {

        LoggerService.getLogger().info("Search steam window for sign in user: $username")
        var hWnd: HWND? = User32Ext.INSTANCE.FindWindow(null, STEAM_SIGN_NAME)
        while (hWnd == null) {
            hWnd = User32Ext.INSTANCE.FindWindow(null, STEAM_SIGN_NAME)
        }

        LoggerService.getLogger().info("Steam is running, waiting for the authorization window to be drawn..")
        while (!isCurrentPage(hWnd, patternLogo, 1.0)) { continue }

        /* Нужно обязательно кликакать на эту хуйню чтобы ввод работа */
        val cefBrowserHwnd = User32Ext.INSTANCE.FindWindowEx(hWnd, null, "CefBrowserWindow", null)
        User32Ext.INSTANCE.PostMessage(cefBrowserHwnd, WH_MOUSE, WPARAM(0), null)

        val widgetHwnd = User32Ext.INSTANCE.FindWindowEx(cefBrowserHwnd, null, "Chrome_WidgetWin_0", null)

        LoggerService.getLogger().info("Entering $username in Steam.")
        postText(widgetHwnd, username)
        postKeyPress(widgetHwnd, VK_TAB.toLong())

        LoggerService.getLogger().info("Entering $password in Steam.")
        postText(widgetHwnd, password)
        postKeyPress(widgetHwnd, VK_ENTER.toLong())
    }

    override suspend fun guard(sharedSecret: String): Boolean {
        try {
            val hWnd = User32Ext.INSTANCE.FindWindow(null, STEAM_SIGN_NAME) ?: return false
            LoggerService.getLogger().info("We wait for the window with the Guard input for $DURATION_WAIT_GUARD seconds")
            if (!isCurrentPage(hWnd, patternGuard, DURATION_WAIT_GUARD)) return false

            val cefBrowserHwnd = User32Ext.INSTANCE.FindWindowEx(hWnd, null, "CefBrowserWindow", null)
            User32Ext.INSTANCE.PostMessage(cefBrowserHwnd, WH_MOUSE, WPARAM(0), null)

            val widgetHwnd = User32Ext.INSTANCE.FindWindowEx(cefBrowserHwnd, null, "Chrome_WidgetWin_0", null)
            postKeyPress(widgetHwnd, VK_TAB.toLong())

            val code = guardSteam.getCode(sharedSecret)
            LoggerService.getLogger().info("Entering guard in Steam | Guard Code: $code")
            postText(widgetHwnd, code)

            return true
        } catch (ignored: Exception) { return false }
    }

}