package com.project.steamfarm.retrofit.api

import com.project.steamfarm.retrofit.response.RSAResponse
import com.project.steamfarm.retrofit.response.RefreshResponse
import com.project.steamfarm.retrofit.response.SteamAuthResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Authentication {

    @GET("GetPasswordRSAPublicKey/v1/")
    fun getRSAPublicKey(@Query("account_name") username: String): Call<RSAResponse>

    @FormUrlEncoded
    @POST("BeginAuthSessionViaCredentials/v1/")
    fun beginAuthSessionViaCredentials(
        @Field("account_name") username: String,
        @Field("encrypted_password") encryptedPassword: String,
        @Field("twofactorcode") twoFactorCode: String = "",
        @Field("emailauth") emailAuth: String = "",
        @Field("loginfriendlyname") loginFriendlyName: String = "",
        @Field("captchagid") captchaGid: String = "-1",
        @Field("captcha_text") captchaText: String = "",
        @Field("emailsteamid") emailSteamId: String = "",
        @Field("encryption_timestamp") encryptionTimestamp: String,
        @Field("remember_login") rememberLogin: Boolean = true,
        @Field("donotcache") doDotCache: String = System.currentTimeMillis().toString(),
    ): Call<SteamAuthResponse>

    @FormUrlEncoded
    @POST("UpdateAuthSessionWithSteamGuardCode/v1/")
    fun updateSessionWithSteamGuard(
        @Field("client_id") clientId: String,
        @Field("steamid") steamId: String,
        @Field("code") code: String,
        @Field("code_type") codeType: String = "3",
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("PollAuthSessionStatus/v1/")
    fun pollAuthSessionStatus(
        @Field("client_id") clientId: String,
        @Field("request_id") requestId: String
    ): Call<RefreshResponse>


}