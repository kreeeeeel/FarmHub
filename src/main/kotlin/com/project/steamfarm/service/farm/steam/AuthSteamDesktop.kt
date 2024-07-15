package com.project.steamfarm.service.farm.steam

import com.project.steamfarm.model.ConfigModel
import com.project.steamfarm.service.farm.Desktop

const val STEAM_SIGN_IN_NAME = "Sign in to Steam"

abstract class AuthSteamDesktop: Desktop() {

    protected val configModel = ConfigModel().fromFile()

    abstract fun start(ipcName: String, gameId: Int)
    abstract fun signIn(username: String, password: String)
    abstract fun guard(sharedSecret: String)

}