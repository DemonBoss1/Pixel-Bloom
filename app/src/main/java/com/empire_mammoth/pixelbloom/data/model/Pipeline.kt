package com.empire_mammoth.pixelbloom.data.model

import com.google.gson.annotations.SerializedName

data class Pipeline(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("nameEn") val nameEn: String,
    @SerializedName("description") val description: String,
    @SerializedName("descriptionEn") val descriptionEn: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("version") val version: Double,
    @SerializedName("status") val status: String,
    @SerializedName("type") val type: String,
    @SerializedName("createdDate") val createdDate: String,
    @SerializedName("lastModified") val lastModified: String
)
