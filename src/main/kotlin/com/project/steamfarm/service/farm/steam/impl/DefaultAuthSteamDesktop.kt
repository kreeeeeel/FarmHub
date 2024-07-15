package com.project.steamfarm.service.farm.steam.impl

import com.project.steamfarm.service.farm.DEFAULT_DURATION
import com.project.steamfarm.service.farm.PATH_TO_IMG
import com.project.steamfarm.service.farm.steam.AuthSteamDesktop
import com.project.steamfarm.service.farm.steam.STEAM_SIGN_IN_NAME
import com.project.steamfarm.service.steam.GuardSteam
import com.project.steamfarm.service.steam.impl.DefaultGuardSteam
import com.project.steamfarm.service.user32.User32
import org.sikuli.script.Pattern
import org.sikuli.script.Region
import java.io.File

private val PAGE_STEAM_PATH = "$PATH_TO_IMG\\steam"
private val PAGE_GUARD_FIELD = "$PAGE_STEAM_PATH\\guard.png"

class DefaultAuthSteamDesktop: AuthSteamDesktop() {

    private val patternGuard = Pattern(PAGE_GUARD_FIELD)

    private val guardSteam: GuardSteam = DefaultGuardSteam()

    init {
        if (!File(PAGE_GUARD_FIELD).exists()) throw NullPointerException("$PAGE_GUARD_FIELD is not found.")
    }

    override fun start(ipcName: String, gameId: Int) {

        val processBuilder = ProcessBuilder()

        val random = (1000..50000).random().toString()
        processBuilder.environment()["VPROJECT"] = random

        val command = listOf(
            configModel.steamExecutor, "-login", "-nofriendsui", "-vgui", "-noreactlogin", "-noverifyfiles", "-nobootstrapupdate",
            "-skipinitialbootstrap", "-norepairfiles", "-overridepackageurl", "-disable-winh264", "-language",
            "english", "-master_ipc_name_override", "$ipcName$random", "-applaunch", "$gameId", "-language", "english",
            "+exec", "autoexec.cfg", "+exec", "gamestate_integration_1.cfg", "-w", "360", "-h", "270", "-console",
            "-condebug", "-conclearlog", "-allowmultiple", "-con_logfile", "$ipcName.log",
            "-swapcores", "-noqueuedload", "-vrdisable", "-windowed", "-nopreload", "-limitvsconst",
            "-softparticlesdefaultoff", "-nohltv", "-noaafonts", "-nosound", "-novid", "+violence_hblood", "0",
            "+sethdmodels", "0", "+mat_disable_fancy_blending", "1", "+r_dynamic", "0", "+engine_no_focus_sleep", "100",
            "-nojoy"
        )
        processBuilder.command(command).start()
    }

    override fun signIn(username: String, password: String) {

        val hWnd = User32.INSTANCE.FindWindow(null, STEAM_SIGN_IN_NAME)
        if (hWnd == null) {
            Thread.sleep(1000)
            signIn(username, password)
            return
        }

        User32.INSTANCE.GetWindowThreadProcessId(hWnd, currentPid)
        User32.INSTANCE.SetForegroundWindow(hWnd)

        val offsetProperties = getOffsetProperties(hWnd)
        val login = Region(offsetProperties.offsetX + 45, offsetProperties.offsetY + 135)
        login.click()
        login.type(username)

        val pass = Region(offsetProperties.offsetX + 45, offsetProperties.offsetY + 210)
        pass.click()
        pass.type(password)

        val signIn = Region(offsetProperties.offsetX + 220, offsetProperties.offsetY + 300)
        signIn.click()
    }

    override fun guard(sharedSecret: String) {

        val hWnd = User32.INSTANCE.FindWindow(null, STEAM_SIGN_IN_NAME) ?: return

        User32.INSTANCE.GetWindowThreadProcessId(hWnd, currentPid)
        User32.INSTANCE.SetForegroundWindow(hWnd)

        val region = getRegion(hWnd)
        val guard = region.wait(patternGuard, DEFAULT_DURATION)

        guard.click()
        guard.type(guardSteam.getCode(sharedSecret))
    }

}