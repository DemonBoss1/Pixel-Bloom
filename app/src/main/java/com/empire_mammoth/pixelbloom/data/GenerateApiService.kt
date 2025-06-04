package com.empire_mammoth.pixelbloom.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface GenerateApiService {
    @GET("key/api/v1/pipelines")
    fun getPipeline(): Response<ResponseBody>
}