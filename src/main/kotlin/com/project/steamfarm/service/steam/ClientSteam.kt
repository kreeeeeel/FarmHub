package com.project.steamfarm.service.steam

import com.project.steamfarm.retrofit.response.SteamProfileResponse

interface ClientSteam {
    fun authentication(username: String, password: String): Boolean
    fun getProfileData(): SteamProfileResponse?
}