package com.project.steamfarm.retrofit.response

import com.google.gson.annotations.SerializedName

data class RSAResponse(
    @SerializedName("response") val response: RSA
)

data class RSA(
    @SerializedName("publickey_mod") val publickeyMod: String,
    @SerializedName("publickey_exp") val publickeyExp: String,
    @SerializedName("timestamp") val timestamp: String,
)