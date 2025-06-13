package com.empire_mammoth.pixelbloom.domain.usecase

import com.empire_mammoth.pixelbloom.domain.repositories.GenerateImageRepository
import javax.inject.Inject

class GetGenerateImageUseCase @Inject constructor(
    private val generateImageRepository: GenerateImageRepository
) {
    suspend operator fun invoke(pipelineId: String, prompt: String): List<String>? {
        return generateImageRepository.getImage(pipelineId, prompt)
    }

}