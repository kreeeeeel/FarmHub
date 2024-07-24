package com.project.panel.service.steam

import com.project.panel.retrofit.response.SteamProfileResponse

interface ClientSteam {
    fun getProfileData(steamId: Long): SteamProfileResponse?
}