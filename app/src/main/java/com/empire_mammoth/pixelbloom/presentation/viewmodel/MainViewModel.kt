package com.empire_mammoth.pixelbloom.presentation.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empire_mammoth.pixelbloom.data.api.GenerateApiService
import com.empire_mammoth.pixelbloom.domain.model.SaveStatus
import com.empire_mammoth.pixelbloom.presentation.model.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    val generateApiService: GenerateApiService) : ViewModel() {

    private var idRequest = 0
    private val _uiState = MutableStateFlow<ViewState>(ViewState(idRequest, null, false))
    val uiState: StateFlow<ViewState> = _uiState

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus

    fun generate(prompt: String) {
        idRequest++
        viewModelScope.launch {
            try {
                val pipeline = generateApiService.getPipeline()
                val generationStatusResponse = generateImage(pipeline[0].id, prompt)
                val bitmap = generationStatusResponse?.firstOrNull()?.let { base64String ->
                    displayBase64Image(base64String)
                }
                _uiState.update { ViewState(idRequest, bitmap, bitmap == null) }
                bitmap?.let { saveImage(it) }
            } catch (e: Exception) {

            }
        }
    }

    private suspend fun generateImage(pipelineId: String, prompt: String): List<String>? {

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

    private fun displayBase64Image(base64String: String): Bitmap? {
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

    fun saveImage(bitmap: Bitmap) = viewModelScope.launch {
        _saveStatus.value = SaveStatus.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val uri = saveImageToPictures(appContext, bitmap, "img_${System.currentTimeMillis()}")
                    uri?.let { SaveStatus.Success(it) } ?: SaveStatus.Error("Failed to save")
                } catch (e: Exception) {
                    SaveStatus.Error(e.message ?: "Unknown error")
                }
            }
            _saveStatus.value = result
        }
    }

    fun saveImageToPictures(context: Context, bitmap: Bitmap, filename: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PixelBloom")
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