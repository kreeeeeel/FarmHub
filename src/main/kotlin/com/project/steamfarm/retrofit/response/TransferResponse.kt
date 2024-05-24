package com.project.steamfarm.retrofit.response

import com.google.gson.annotations.SerializedName

data class TransferResponse(
    @SerializedName("steamID") val steamID: String,
    @SerializedName("transfer_info") val transferInfo: List<TransferInfoResponse>
)

data class TransferInfoResponse(
    @SerializedName("url") val url: String,
    @SerializedName("params") val params: TransferParams,
)

data class TransferParams(
    @SerializedName("nonce") val nonce: String,
    @SerializedName("auth") val auth: String,
)