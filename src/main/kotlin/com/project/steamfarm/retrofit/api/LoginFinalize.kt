package com.project.steamfarm.retrofit.api

import com.project.steamfarm.retrofit.response.TransferResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginFinalize {

    @FormUrlEncoded
    @POST("jwt/finalizelogin")
    fun finalizeLogin(
        @Field("nonce") nonce: String,
        @Field("sessionid") sessionId: String,
        @Field("redir") reDir: String = "https://steamcommunity.com/login/home/?goto=",
        @Header("accept") accept: String = "application/json, text/plain, */*",
        @Header("sec-fetch-site") secFetchSite: String = "cross-site",
        @Header("sec-fetch-mode") secFetchMode: String = "cors",
        @Header("sec-fetch-dest") secFetchDest: String = "empty",
    ): Call<TransferResponse>

}