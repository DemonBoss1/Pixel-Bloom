package com.empire_mammoth.pixelbloom.data.repositories

import com.empire_mammoth.pixelbloom.data.api.GenerateApiService
import com.empire_mammoth.pixelbloom.data.model.Pipeline
import com.empire_mammoth.pixelbloom.domain.repositories.GenerateImageRepository
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class GenerateImageRepositoryImpl @Inject constructor(
    private val generateApiService: GenerateApiService
) : GenerateImageRepository {
    override suspend fun getImage(pipelineId: String, prompt: String): List<String>? {

        val paramsJson = """
            {"type": "GENERATE", "numImages": 1, "width": 1024, "height": 1024, "generateParams": {"query": "$prompt"}}
        """.trimIndent()

        val pipelineIdBody = pipelineId.toRequestBody("text/plain".toMediaTypeOrNull())
        val paramsBody = paramsJson.toRequestBody("application/json".toMediaTypeOrNull())

        val generationStatusResponse = generateApiService.generateImage(pipelineIdBody, paramsBody)

        var attempts = 60
        while (attempts > 0) {
            val status = generateApiService.getGenerationStatus(generationStatusResponse.uuid)
            if (status.status == "DONE") return status.result?.files
            attempts -= 1
            delay(1000)
        }
        return null
    }

    override suspend fun getPipeline(): List<Pipeline> {
        return generateApiService.getPipeline()
    }
}