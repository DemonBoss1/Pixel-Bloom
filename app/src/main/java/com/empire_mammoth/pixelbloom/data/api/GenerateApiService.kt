package com.empire_mammoth.pixelbloom.data.api

import com.empire_mammoth.pixelbloom.data.model.GenerationStatusResponse
import com.empire_mammoth.pixelbloom.data.model.Pipeline
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GenerateApiService {
    @GET("key/api/v1/pipelines")
    suspend fun getPipeline(): List<Pipeline>

    @Multipart
    @POST("key/api/v1/pipeline/run") // Replace with the correct endpoint
    suspend fun generateImage(
        @Part("pipeline_id") pipelineId: RequestBody,
        @Part("params") params: RequestBody
    ): GenerationStatusResponse
}