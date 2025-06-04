package com.empire_mammoth.pixelbloom.data.api

import com.empire_mammoth.pixelbloom.data.model.Pipeline
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface GenerateApiService {
    @GET("key/api/v1/pipelines")
    suspend fun getPipeline(): List<Pipeline>
}