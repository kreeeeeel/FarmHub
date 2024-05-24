package com.project.steamfarm.service.steam.impl

import com.google.gson.GsonBuilder
import com.project.steamfarm.data.RSAParam
import com.project.steamfarm.retrofit.api.Authentication
import com.project.steamfarm.retrofit.api.LoginFinalize
import com.project.steamfarm.retrofit.api.Transfer
import com.project.steamfarm.retrofit.response.RefreshTokenResponse
import com.project.steamfarm.retrofit.response.SteamDataResponse
import com.project.steamfarm.retrofit.response.TransferResponse
import com.project.steamfarm.service.encrypto.PasswordEncryptor
import com.project.steamfarm.service.encrypto.impl.DefaultPasswordEncryptor
import com.project.steamfarm.service.steam.AuthSteam
import com.project.steamfarm.service.steam.GuardSteam
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.security.spec.RSAPublicKeySpec
import java.util.*

class DefaultAuthSteam: AuthSteam {

    private val gsonConverterFactory = GsonConverterFactory.create(
        GsonBuilder()
            .setLenient()
            .create()
    )

    private val steamAuthClient = Retrofit.Builder()
        .baseUrl("https://api.steampowered.com/IAuthenticationService/")
        .addConverterFactory(gsonConverterFactory).build()

    private val steamLoginClient = Retrofit.Builder()
        .baseUrl("https://login.steampowered.com/")
        .addConverterFactory(gsonConverterFactory).build()

    private val steamTransferClient = Retrofit.Builder()
        .baseUrl("https://steamcommunity.com/login/")
        .addConverterFactory(gsonConverterFactory).build()

    private val steamAuthApi = steamAuthClient.create(Authentication::class.java)
    private val steamLoginApi = steamLoginClient.create(LoginFinalize::class.java)
    private val steamTransferApi = steamTransferClient.create(Transfer::class.java)

    private val passwordEncryptor: PasswordEncryptor = DefaultPasswordEncryptor()
    private val steamGuard: GuardSteam = DefaultGuardSteam()


    override fun fetchRSAParam(username: String): RSAParam? {
        val response = steamAuthApi.getRSAPublicKey(username = username)
            .execute()

        val body = response.body()?.response ?: return null
        return RSAParam(
            RSAPublicKeySpec(
                BigInteger(body.publickeyMod, 16),
                BigInteger(body.publickeyExp, 16)
            ),
            body.timestamp.toLong()
        )
    }

    override fun beginAuth(username: String, pass: String, param: RSAParam): SteamDataResponse? {
        val encryptedPass = passwordEncryptor.encrypt(param.pubKeySpecval, pass) ?: return null
        val response = steamAuthApi.beginAuthSessionViaCredentials(
            username = username,
            encryptedPassword = encryptedPass,
            encryptionTimestamp = param.timestamp.toString()
        ).execute()

        val body = response.body() ?: return null
        return body.response
    }

    override fun updateSessionWithSteamGuard(steamId: String, sharedSecret: String, clientId: String): Boolean {
        val response = steamAuthApi.updateSessionWithSteamGuard(
            steamId = steamId,
            clientId = clientId,
            code = steamGuard.getCode(sharedSecret)
        ).execute()

        return response.body() != null
    }

    override fun pollLoginStatus(clientId: String, requestId: String): RefreshTokenResponse? {
        val response = steamAuthApi.pollAuthSessionStatus(clientId = clientId, requestId = requestId).execute()
        return response.body()?.response

    }

    override fun finalizeLogin(refreshToken: String): TransferResponse? {
        val response = steamLoginApi.finalizeLogin(nonce = refreshToken, sessionId = getRandomHexString()).execute()
        return response.body()
    }

    override fun getCommunityCookie(transferResponse: TransferResponse): String? {

        val transferInfo = transferResponse.transferInfo.firstOrNull {
            it.url.startsWith(
                steamTransferClient.baseUrl().url().toString()
            )
        } ?: return null

        val response = steamTransferApi.transfer(
            nonce = transferInfo.params.nonce,
            auth = transferInfo.params.auth,
            steamId = transferResponse.steamID
        ).execute()

        val cookie = response.headers()["Set-Cookie"] ?: return null
        val index = cookie.indexOf(";")
        if (index == -1) {
            return null
        }

        return URLDecoder.decode(cookie.substring(0, index), StandardCharsets.UTF_8.toString())
    }

    private fun getRandomHexString(): String {
        val count = 12
        val random = Random()
        val stringBuffer = StringBuffer()

        while (stringBuffer.length < count) {
            stringBuffer.append(Integer.toHexString(random.nextInt()))
        }
        return stringBuffer.toString().substring(0, count)
    }

}