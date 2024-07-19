package com.project.steamfarm.service.farm.steam.impl

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.HEIGHT_APP
import com.project.steamfarm.model.WIDTH_APP
import com.project.steamfarm.service.farm.PATH_TO_IMG
import com.project.steamfarm.service.farm.User32Ext
import com.project.steamfarm.service.farm.steam.GameDesktop
import com.project.steamfarm.service.logger.LoggerService
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import kotlinx.coroutines.delay
import org.sikuli.script.Pattern
import java.io.File
import java.io.FileNotFoundException

private const val DOTA_GAME_NAME = "Dota 2"
private const val DOTA_OFFSET_NEW_USER_X = 200
private const val DOTA_OFFSET_NEW_USER_Y = 115

private const val DOTA_OFFSET_MENU_X = 60
private const val DOTA_OFFSET_MENU_Y = 5

private const val DOTA_OFFSET_FRIEND_X = 65
private const val DOTA_OFFSET_FRIEND_Y = 90

private const val DOTA_OFFSET_SEARCH_FIELD_X = 160
private const val DOTA_OFFSET_SEARCH_FIELD_Y = 100

private const val DOTA_OFFSET_SEARCH_X = 165
private const val DOTA_OFFSET_SEARCH_Y = 125

private val DOTA2_PATH = "$PATH_TO_IMG\\dota2"

private val DOTA2_START = "$DOTA2_PATH\\start.png"
private val DOTA2_NEW_USER = "$DOTA2_PATH\\new_user.png"
private val DOTA2_MENU = "$DOTA2_PATH\\dota_menu.png"
private val DOTA2_ACTIVE_MENU = "$DOTA2_PATH\\dota_menu_active.png"
private val DOTA2_INVITE_MENU = "$DOTA2_PATH\\invite.png"
private val DOTA2_NEW_ITEM = "$DOTA2_PATH\\buttons\\accept_item.png"
private val DOTA2_SKIP_NEWS = "$DOTA2_PATH\\buttons\\skip_news.png"

private const val CURRENT_SIMILAR = 0.95

class DotaGameDesktop: GameDesktop() {

    private val startPattern = Pattern(DOTA2_START).similar(CURRENT_SIMILAR)
    private val newUserPattern = Pattern(DOTA2_NEW_USER).similar(CURRENT_SIMILAR)
    private val newItemPattern = Pattern(DOTA2_NEW_ITEM).similar(CURRENT_SIMILAR)
    private val skipNewsPattern = Pattern(DOTA2_SKIP_NEWS).similar(CURRENT_SIMILAR)
    private val menuActivePattern = Pattern(DOTA2_ACTIVE_MENU).similar(CURRENT_SIMILAR)
    private val menuPattern = Pattern(DOTA2_MENU).similar(CURRENT_SIMILAR)
    private val invitePattern = Pattern(DOTA2_INVITE_MENU).similar(CURRENT_SIMILAR)

    init {
        if (!File(DOTA2_START).exists()) throw FileNotFoundException("$DOTA2_START is not found!")
        if (!File(DOTA2_NEW_USER).exists()) throw FileNotFoundException("$DOTA2_NEW_USER is not found!")
        if (!File(DOTA2_NEW_ITEM).exists()) throw FileNotFoundException("$DOTA2_NEW_ITEM is not found!")
        if (!File(DOTA2_SKIP_NEWS).exists()) throw FileNotFoundException("$DOTA2_SKIP_NEWS is not found!")
        if (!File(DOTA2_ACTIVE_MENU).exists()) throw FileNotFoundException("$DOTA2_ACTIVE_MENU is not found!")
        if (!File(DOTA2_MENU).exists()) throw FileNotFoundException("$DOTA2_MENU is not found!")
        if (!File(DOTA2_INVITE_MENU).exists()) throw FileNotFoundException("$DOTA2_INVITE_MENU is not found!")
    }

    override fun setConfig() {}

    override fun getCommand(): List<String> = listOf(
        "-applaunch", "570", "-language", "english", "-w", "$WIDTH_APP", "-h", "$HEIGHT_APP",
        "+map_enable_background_maps", "0", "+fps_max", "40", "-dota_embers", "0", "-autoconfig_level", "0", "-nosound",
        "-maxtextureres", "2", "-novid", "-nosync", "-conclearlog", "-swapcores", "-noqueuedload", "-vrdisable",
        "-windowed", "-nopreload", "-limitvsconst", "-softparticlesdefaultoff", "-nod3d9ex", "-noipx", "-nocra"
    )

    override suspend fun setName(hWnd: HWND, username: String) {
        val name = String.format(langApplication.text.farm.service.dota, username)
        LoggerService.getLogger().info("Changing Dota2 window name to $name")
        User32Ext.INSTANCE.SetWindowText(hWnd, name)
    }

    override suspend fun getGameHwnd(): HWND {
        LoggerService.getLogger().info("Search dota2 window")
        var hWnd = User32.INSTANCE.FindWindow(null, DOTA_GAME_NAME)
        while (hWnd == null) {
            hWnd = User32.INSTANCE.FindWindow(null, DOTA_GAME_NAME)
        }
        return hWnd
    }

    override suspend fun setReadyGame(hWnd: HWND) {

        var isReady = false
        while (isCurrentPage(hWnd, startPattern, 1.0)) { continue }

        while (!isReady) {
            if (isCurrentPage(hWnd, menuPattern, 1.0) || isCurrentPage(hWnd, menuActivePattern, 1.0)) {
                isReady = true
            }
            else if (isCurrentPage(hWnd, newUserPattern, 1.0)) skipNewUser(hWnd)
            else if (isCurrentPage(hWnd, newItemPattern, 1.0)) skipNewItem(hWnd)
            else if (isCurrentPage(hWnd, skipNewsPattern, 1.0)) skipNews(hWnd)
        }

        clickToMainMenu(hWnd)
    }

    override suspend fun makeInvite(hWnd: HWND, steamId: String) {
        LoggerService.getLogger().info("Start invite in lobby: $steamId")
        clickToMainMenu(hWnd)

        delay(100)
        val offsetProperties = getOffsetProperties(hWnd)
        val offsetFriendX = offsetProperties.offsetX + DOTA_OFFSET_FRIEND_X
        val offsetFriendY = offsetProperties.offsetY + DOTA_OFFSET_FRIEND_Y
        click(hWnd, offsetFriendX, offsetFriendY)

        delay(100)
        val offsetFieldX = offsetProperties.offsetX + DOTA_OFFSET_SEARCH_FIELD_X
        val offsetFieldY = offsetProperties.offsetY + DOTA_OFFSET_SEARCH_FIELD_Y
        click(hWnd, offsetFieldX, offsetFieldY)

        postText(hWnd, steamId)
        delay(100)

        val offsetSearchX = offsetProperties.offsetX + DOTA_OFFSET_SEARCH_X
        val offsetSearchY = offsetProperties.offsetY + DOTA_OFFSET_SEARCH_Y
        click(hWnd, offsetSearchX, offsetSearchY)

        delay(100)
        val region = getRegion(hWnd)
        region.wait(invitePattern, 15.0).click()

        delay(100)
        clickToMainMenu(hWnd)
    }

    private suspend fun skipNewUser(hWnd: HWND) {
        val offsetProperties = getOffsetProperties(hWnd)
        IntRange(0, 5).forEach { _ ->

            val offsetX = offsetProperties.offsetX + DOTA_OFFSET_NEW_USER_X
            val offsetY = offsetProperties.offsetY + DOTA_OFFSET_NEW_USER_Y

            click(hWnd, offsetX, offsetY)
            delay(10)
        }
        delay(1000)
    }

    private suspend fun skipNewItem(hWnd: HWND) = try {
        val region = getRegion(hWnd)
        region.wait(newItemPattern).click()
        delay(1000)
    } catch (ignored: Exception) {}

    private suspend fun skipNews(hWnd: HWND) = try {
        val region = getRegion(hWnd)
        region.wait(skipNewsPattern).click()
        delay(1000)
    } catch (ignored: Exception) {}

    private fun clickToMainMenu(hWnd: HWND) {
        val offsetProperties = getOffsetProperties(hWnd)
        val offsetX = offsetProperties.offsetX + DOTA_OFFSET_MENU_X
        val offsetY = offsetProperties.offsetY + DOTA_OFFSET_MENU_Y
        click(hWnd, offsetX, offsetY)
    }

}