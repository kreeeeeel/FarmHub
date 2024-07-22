package com.project.steamfarm.service.farm.steam

import com.project.steamfarm.service.farm.Desktop

const val STEAM_SIGN_NAME = "Sign in to Steam"

abstract class SteamDesktop: Desktop() {

    lateinit var gameDesktop: GameDesktop

    abstract suspend fun initGame(gameDesktop: GameDesktop)
    abstract suspend fun start(ipcName: String)
    abstract suspend fun signIn(username: String, password: String)
    abstract suspend fun guard(sharedSecret: String): Boolean
}