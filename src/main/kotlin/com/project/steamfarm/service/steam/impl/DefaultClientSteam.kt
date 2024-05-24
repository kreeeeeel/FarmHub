package com.project.steamfarm.service.steam.impl

import com.project.steamfarm.repository.impl.MaFileRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.service.steam.AuthSteam
import com.project.steamfarm.service.steam.ClientSteam

class DefaultClientSteam: ClientSteam {

    var steamCookie: String? = null
    var steamId: String? = null

    var isLoggedIn: Boolean = false

    private val steamAuthClient: AuthSteam = DefaultAuthSteam()

    override fun authentication(username: String, password: String): Boolean {
        val maFile = MaFileRepository().findById(username) ?: return false

        val fetchRSAParam = steamAuthClient.fetchRSAParam(username) ?: return false

        val beginAuth = steamAuthClient.beginAuth(username, password, fetchRSAParam) ?: return false
        if (beginAuth.isNullable()){
            return false
        }

        val id = beginAuth.steamId ?: return false
        val clientId = beginAuth.clientId ?: return false
        val requestId = beginAuth.requestId ?: return false

        if( !steamAuthClient.updateSessionWithSteamGuard(id, maFile.sharedSecret, clientId) ){
            return false
        }

        val pollLoginStatus = steamAuthClient.pollLoginStatus(clientId, requestId) ?: return false
        if (pollLoginStatus.isNullable()) {
            return false
        }

        val refreshToken = pollLoginStatus.refreshToken ?: return false
        val finalizeLogin = steamAuthClient.finalizeLogin(refreshToken) ?: return false

        val cookie = steamAuthClient.getCommunityCookie(finalizeLogin)
        if (cookie.isNullOrEmpty()){
            return false
        }

        steamCookie = cookie
        isLoggedIn = true
        steamId = id

        return true
    }
}