package com.project.steamfarm.retrofit.response

import com.google.gson.annotations.SerializedName

data class SteamAuthResponse(
    val response: SteamDataResponse
)

data class SteamDataResponse(
    @SerializedName("steamid") val steamId: String?,
    @SerializedName("client_id") val clientId: String?,
    @SerializedName("request_id") val requestId: String?,
) {
    fun isNullable() = steamId.isNullOrEmpty() || clientId.isNullOrEmpty() || requestId.isNullOrEmpty()
}