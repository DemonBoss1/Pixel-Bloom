package com.empire_mammoth.pixelbloom.data.model

import com.google.gson.annotations.SerializedName

data class GenerationStatusResponse(
    @SerializedName("status") val status: String,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("status_time") val statusTime: Int
)
