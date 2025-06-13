package com.empire_mammoth.pixelbloom.domain.usecase

import com.empire_mammoth.pixelbloom.data.model.Pipeline
import com.empire_mammoth.pixelbloom.domain.repositories.GenerateImageRepository
import javax.inject.Inject

class GetPipelineUseCase @Inject constructor(
    private val generateImageRepository: GenerateImageRepository
) {
    suspend operator fun invoke(): Pipeline {
        return generateImageRepository.getPipeline()[0]
    }
}