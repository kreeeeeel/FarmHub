package com.project.steamfarm.retrofit.api

import com.project.steamfarm.retrofit.response.SteamProfileResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface Profile {

    @GET("profiles/{id}")
    fun getProfile(@Path("id") id: String, @Query("xml") xml: Int = 1): Call<SteamProfileResponse>

    @GET("profiles/{id}/tradeoffers/privacy")
    fun getTradeLink(@Path("id") id: String, @Header("Cookie") cookie: String): Call<ResponseBody>

}