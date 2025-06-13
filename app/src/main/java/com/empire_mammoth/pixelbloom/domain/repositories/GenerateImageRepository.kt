package com.empire_mammoth.pixelbloom.domain.repositories

import com.empire_mammoth.pixelbloom.data.model.Pipeline

interface GenerateImageRepository {
    suspend fun getImage(pipelineId: String, prompt: String) : List<String>?
    suspend fun getPipeline() : List<Pipeline>
}