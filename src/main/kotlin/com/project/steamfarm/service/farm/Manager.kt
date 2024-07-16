package com.project.steamfarm.service.farm

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.service.farm.steam.impl.AuthSteamDesktopImpl
import com.project.steamfarm.service.farm.steam.impl.DotaGameDesktop

const val SWP_NOSIZE = 0x0001
const val SWP_NOZORDER = 0x0004

class Manager {

    private var userModels: MutableList<UserModel> = mutableListOf()

    private val authSteamDesktop = AuthSteamDesktopImpl()

    fun initUser(userModels: MutableList<UserModel>) {
        this.userModels = userModels
    }

    fun launchGame(gameId: Int): Boolean {

        if (userModels.size != 10) return false

        var vertical = 0
        var horizontal = 0

        userModels.forEach { userModel ->

            authSteamDesktop.start(userModel.steam.accountName, gameId)
            authSteamDesktop.signIn(userModel.steam.accountName, userModel.steam.password)
            authSteamDesktop.guard(userModel.steam.sharedSecret)

            val dotaDesktop = DotaGameDesktop()
            val hwnd = dotaDesktop.gameLaunched() ?: throw NullPointerException("Dota is not running")

            val name = String.format(langApplication.text.farm.service.dota, userModel.steam.accountName)

            val offsetProperty = dotaDesktop.getOffsetProperties(hwnd)
            val offsetX = horizontal++ * offsetProperty.width
            val offsetY = vertical * offsetProperty.height

            if (horizontal >= 3) {
                horizontal = 0
                vertical++
            }

            User32Ext.INSTANCE.SetWindowTextA(hwnd, name)
            User32Ext.INSTANCE.SetWindowPos(hwnd, null, offsetX, offsetY, 0, 0, SWP_NOSIZE or SWP_NOZORDER)
        }
        return true
    }

}