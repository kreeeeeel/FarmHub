package com.project.steamfarm.service.steam.impl

import com.project.steamfarm.retrofit.api.Profile
import com.project.steamfarm.retrofit.response.SteamProfileResponse
import com.project.steamfarm.service.steam.AuthSteam
import com.project.steamfarm.service.steam.ClientSteam
import retrofit2.Retrofit
import retrofit2.converter.simplexml.*

@Suppress("DEPRECATION")
class DefaultClientSteam: ClientSteam {

    private var steamCookie: String? = null
    private var steamId: String? = null

    private var isLoggedIn: Boolean = false

    private val clientXml = Retrofit.Builder()
        .baseUrl("https://steamcommunity.com/")
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()

    private val apiXml = clientXml.create(Profile::class.java)

    private val steamAuthClient: AuthSteam = DefaultAuthSteam()

    override fun authentication(username: String, password: String, sharedSecret: String): Boolean {

        val fetchRSAParam = steamAuthClient.fetchRSAParam(username) ?: return false

        val beginAuth = steamAuthClient.beginAuth(username, password, fetchRSAParam) ?: return false
        if (beginAuth.isNullable()){
            return false
        }

        val id = beginAuth.steamId ?: return false
        val clientId = beginAuth.clientId ?: return false
        val requestId = beginAuth.requestId ?: return false

        if( !steamAuthClient.updateSessionWithSteamGuard(id, sharedSecret, clientId) ){
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

    override fun getProfileData(): SteamProfileResponse? {
        if (!isLoggedIn || steamId == null) return null
        return apiXml.getProfile(steamId!!).execute().body()
    }

}