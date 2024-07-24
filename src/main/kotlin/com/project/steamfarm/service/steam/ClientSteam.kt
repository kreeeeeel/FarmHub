package com.project.steamfarm.service.steam

import com.project.steamfarm.retrofit.response.SteamProfileResponse

interface ClientSteam {
    fun getProfileData(steamId: Long): SteamProfileResponse?
}