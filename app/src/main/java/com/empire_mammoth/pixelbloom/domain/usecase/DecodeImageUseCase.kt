package com.empire_mammoth.pixelbloom.domain.usecase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import javax.inject.Inject

class DecodeImageUseCase @Inject constructor() {
    operator fun invoke(base64String: String): Bitmap? {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            println("Error decoding Base64 string: ${e.message}")
            e.printStackTrace()
        } catch (e: Exception) {
            println("Error displaying image: ${e.message}")
            e.printStackTrace()
        }
        return null
    }
}