package com.project.steamfarm.service.steam.impl

import com.project.steamfarm.retrofit.api.ProfileApi
import com.project.steamfarm.retrofit.response.SteamProfileResponse
import com.project.steamfarm.service.steam.ClientSteam
import retrofit2.Retrofit
import retrofit2.converter.simplexml.*

@Suppress("DEPRECATION")
class DefaultClientSteam: ClientSteam {

    private val clientXml = Retrofit.Builder()
        .baseUrl("https://steamcommunity.com/")
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build().create(ProfileApi::class.java)

    override fun getProfileData(steamId: Long): SteamProfileResponse? =
        clientXml.getProfile(steamId).execute().body()
}
