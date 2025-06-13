package com.empire_mammoth.pixelbloom.data.repositories

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.empire_mammoth.pixelbloom.domain.repositories.SaveRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SaveRepository {
    override suspend fun saveImage(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            try {
                val uri = saveImageToGallery(context, bitmap, "img_${System.currentTimeMillis()}")
                uri?.let { Result.success(it) } ?: Result.failure(Exception("Failed to save image"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun saveImageToGallery(context: Context, bitmap: Bitmap, filename: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/PixelBloom"
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        return try {
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                resolver.openOutputStream(it)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
                uri
            }
        } catch (e: Exception) {
            Log.e("SaveImage", "Ошибка: ${e.message}")
            null
        }
    }
}