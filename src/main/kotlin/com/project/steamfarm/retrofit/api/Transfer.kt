package com.project.steamfarm.retrofit.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Transfer {

    @FormUrlEncoded
    @POST("settoken")
    fun transfer(
        @Field("nonce") nonce: String,
        @Field("auth") auth: String,
        @Field("steamID") steamId: String,
    ): Call<ResponseBody>

}