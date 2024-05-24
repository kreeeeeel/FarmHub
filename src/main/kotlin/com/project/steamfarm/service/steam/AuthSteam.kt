package com.project.steamfarm.service.steam

import com.project.steamfarm.data.RSAParam
import com.project.steamfarm.retrofit.response.RefreshTokenResponse
import com.project.steamfarm.retrofit.response.SteamDataResponse
import com.project.steamfarm.retrofit.response.TransferResponse

interface AuthSteam {
    fun fetchRSAParam(username: String): RSAParam?
    fun beginAuth(username: String, pass: String, param: RSAParam): SteamDataResponse?
    fun updateSessionWithSteamGuard(steamId: String, sharedSecret: String, clientId: String): Boolean
    fun pollLoginStatus(clientId: String, requestId: String): RefreshTokenResponse?
    fun finalizeLogin(refreshToken: String): TransferResponse?
    fun getCommunityCookie(transferResponse: TransferResponse): String?
}