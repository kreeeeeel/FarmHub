package com.project.panel.retrofit.api

import com.project.panel.retrofit.response.SteamProfileResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileApi {

    @GET("profiles/{id}")
    fun getProfile(@Path("id") id: Long, @Query("xml") xml: Int = 1): Call<SteamProfileResponse>

}