package com.empire_mammoth.pixelbloom.data.model

import com.google.gson.annotations.SerializedName

data class GenerationResult(
    @SerializedName("files") val files: List<String>
)

data class GenerationResponseWithResult(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("status") val status: String,
    @SerializedName("result") val result: GenerationResult?
)