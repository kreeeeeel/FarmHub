package com.project.steamfarm.service.farm

import com.project.steamfarm.model.UserModel
import com.project.steamfarm.service.farm.steam.GameDesktop
import com.project.steamfarm.service.farm.steam.impl.DotaGameDesktop
import com.project.steamfarm.service.farm.steam.impl.SteamDesktopImpl
import com.project.steamfarm.service.logger.LoggerService
import com.sun.jna.platform.win32.WinDef.HWND
import kotlinx.coroutines.*
import java.util.concurrent.Executors

val STEAM_PATH = "$PATH_TO_IMG\\steam"

const val SWP_NOSIZE = 0x0001
const val SWP_NOZORDER = 0x0004

object Manager: Desktop() {

    private val coroutineScope = CoroutineScope(Executors.newFixedThreadPool(10).asCoroutineDispatcher())

    private val dotaDesktop: GameDesktop = DotaGameDesktop()

    private var userModels: MutableList<UserModel> = mutableListOf()
    private lateinit var currentGame: GameDesktop

    // UI
    private lateinit var currentUserName: String

    // Variables for position on window
    private var offsetVertical = 0
    private var offsetHorizontal = 0

    fun initUser(userModels: MutableList<UserModel>) {
        this.userModels = userModels
    }

    fun initGame(gameId: Int) {
        when (gameId) {
            570 -> currentGame = dotaDesktop
            else -> throw IllegalStateException("There is no implementation for this game.")
        }
    }

    fun launchGame() = coroutineScope.launch {

        if (userModels.size != 10) throw IllegalStateException("Users must be 10!")

        val steamDesktop = SteamDesktopImpl(currentGame)
        //val userModel = userModels[0]
        userModels.forEachIndexed { index, userModel ->

            LoggerService.getLogger().info("Start account #${index + 1} for the farm")

            currentUserName = userModel.steam.accountName
            steamDesktop.start(userModel.steam.accountName)
            steamDesktop.signIn(userModel.steam.accountName, userModel.steam.password)
            val isEntered = steamDesktop.guard(userModel.steam.sharedSecret)
            if (!isEntered) {
                LoggerService.getLogger().warning("User ${userModel.steam.accountName} is not entered!")
            } else {
                LoggerService.getLogger().info("User ${userModel.steam.accountName} is entered!")
            }

            val closeJob = launch {
                currentGame.closeSupport()
                currentGame.closeCloudConflict()
            }

            val hwnd = currentGame.getGameHwnd().also {
                currentGame.setName(it, userModel.steam.accountName)
                setOffsetHwnd(it)
            }

            closeJob.cancel()
            System.gc()
        }
    }

    private fun setOffsetHwnd(hwnd: HWND) {
        val offsetProperties = getOffsetProperties(hwnd)
        val offsetX = offsetHorizontal++ * offsetProperties.width
        val offsetY = offsetVertical * offsetProperties.height

        if (offsetHorizontal >= 5) {
            offsetHorizontal = 0
            offsetVertical++
        }

        LoggerService.getLogger().info("Changing the Dota2 window position for $currentUserName | X=$offsetX Y=$offsetY")
        User32Ext.INSTANCE.SetWindowPos(hwnd, null, offsetX, offsetY, 0, 0, SWP_NOSIZE or SWP_NOZORDER)
        System.gc()
    }
}
