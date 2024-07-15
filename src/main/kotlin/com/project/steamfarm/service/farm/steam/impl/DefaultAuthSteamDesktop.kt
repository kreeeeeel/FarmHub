package com.project.steamfarm.service.farm.steam.impl

import com.project.steamfarm.service.farm.DEFAULT_DURATION
import com.project.steamfarm.service.farm.PATH_TO_IMG
import com.project.steamfarm.service.farm.steam.AuthSteamDesktop
import com.project.steamfarm.service.farm.steam.STEAM_SIGN_IN_NAME
import com.project.steamfarm.service.steam.GuardSteam
import com.project.steamfarm.service.steam.impl.DefaultGuardSteam
import com.project.steamfarm.service.user32.User32
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.INPUT
import org.sikuli.script.Pattern
import java.io.File

val charToVK = mapOf(
    'a' to 0x41, 'b' to 0x42, 'c' to 0x43, 'd' to 0x44, 'e' to 0x45, 'f' to 0x46, 'g' to 0x47,
    'h' to 0x48, 'i' to 0x49, 'j' to 0x4A, 'k' to 0x4B, 'l' to 0x4C, 'm' to 0x4D, 'n' to 0x4E,
    'o' to 0x4F, 'p' to 0x50, 'q' to 0x51, 'r' to 0x52, 's' to 0x53, 't' to 0x54, 'u' to 0x55,
    'v' to 0x56, 'w' to 0x57, 'x' to 0x58, 'y' to 0x59, 'z' to 0x5A,
    'A' to 0x41, 'B' to 0x42, 'C' to 0x43, 'D' to 0x44, 'E' to 0x45, 'F' to 0x46, 'G' to 0x47,
    'H' to 0x48, 'I' to 0x49, 'J' to 0x4A, 'K' to 0x4B, 'L' to 0x4C, 'M' to 0x4D, 'N' to 0x4E,
    'O' to 0x4F, 'P' to 0x50, 'Q' to 0x51, 'R' to 0x52, 'S' to 0x53, 'T' to 0x54, 'U' to 0x55,
    'V' to 0x56, 'W' to 0x57, 'X' to 0x58, 'Y' to 0x59, 'Z' to 0x5A
)

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
        User32.INSTANCE.SetFocus(hWnd)

        password.toCharArray().forEach { char ->
            val vkCode = charToVK[char] ?: char.code
            val input = INPUT()

            input.type = WinDef.DWORD(INPUT.INPUT_KEYBOARD.toLong())
            input.input.setType("ki")
            input.input.ki.wScan = WinDef.WORD(0)
            input.input.ki.time = WinDef.DWORD(0)
            input.input.ki.dwExtraInfo = ULONG_PTR(0)

            if (char.isUpperCase()) {
                input.input.ki.wVk = WinDef.WORD(0x10)
                input.input.ki.dwFlags = WinDef.DWORD(0)
                User32.INSTANCE.SendInput(WinDef.DWORD(1), arrayOf(input), input.size())
            }

            input.input.ki.wVk = WinDef.WORD(vkCode.toLong())
            input.input.ki.dwFlags = WinDef.DWORD(0) // keydown
            User32.INSTANCE.SendInput(WinDef.DWORD(1), arrayOf(input), input.size())

            input.input.ki.dwFlags = WinDef.DWORD(WinUser.KEYBDINPUT.KEYEVENTF_KEYUP.toLong())
            User32.INSTANCE.SendInput(WinDef.DWORD(1), arrayOf(input), input.size())

            if (char.isUpperCase()) {
                input.input.ki.wVk = WinDef.WORD(0x10)
                input.input.ki.dwFlags = WinDef.DWORD(WinUser.KEYBDINPUT.KEYEVENTF_KEYUP.toLong())
                User32.INSTANCE.SendInput(WinDef.DWORD(1), arrayOf(input), input.size())
            }
        }
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