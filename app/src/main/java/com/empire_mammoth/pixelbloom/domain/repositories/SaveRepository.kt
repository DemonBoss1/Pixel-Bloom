package com.empire_mammoth.pixelbloom.domain.repositories

import android.graphics.Bitmap

interface SaveRepository {
    suspend fun saveImage(bitmap: Bitmap)
}