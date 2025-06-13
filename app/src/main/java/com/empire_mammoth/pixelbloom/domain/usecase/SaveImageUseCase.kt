package com.empire_mammoth.pixelbloom.domain.usecase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.empire_mammoth.pixelbloom.domain.repositories.SaveRepository
import javax.inject.Inject

class SaveImageUseCase @Inject constructor(
    val saveRepository: SaveRepository
) {
    suspend operator fun invoke(bitmap: Bitmap) {
        saveRepository.saveImage(bitmap)
    }
}