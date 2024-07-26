package com.project.panel.data

import com.google.gson.annotations.SerializedName

data class SteamData(
    @SerializedName("password") var password: String?,
    @SerializedName("shared_secret") val sharedSecret: String,
    @SerializedName("serial_number") val serialNumber: String?,
    @SerializedName("revocation_code") val revocationCode: String?,
    @SerializedName("uri") val uri: String?,
    @SerializedName("server_time") val serverTime: Long?,
    @SerializedName("account_name") val accountName: String,
    @SerializedName("token_gid") val tokenGid: String?,
    @SerializedName("identity_secret") val identitySecret: String?,
    @SerializedName("secret_1") val secret1: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("device_id") val deviceId: String?,
    @SerializedName("fully_enrolled") val fullyEnrolled: Boolean?,
    @SerializedName("Session") val session: Session?,
)

data class Session(
    @SerializedName("SteamID") val steamID: Long?,
    @SerializedName("AccessToken") val accessToken: String?,
    @SerializedName("RefreshToken") val refreshToken: String?,
    @SerializedName("SessionID") val sessionID: String?
)
