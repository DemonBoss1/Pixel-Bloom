package com.empire_mammoth.pixelbloom.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empire_mammoth.pixelbloom.data.api.GenerateApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val generateApiService: GenerateApiService) : ViewModel() {

    private val _uiState = MutableStateFlow<Bitmap?>(null)
    val uiState: StateFlow<Bitmap?> = _uiState

    fun generate(prompt: String) {
        viewModelScope.launch {
            try {
                val pipeline = generateApiService.getPipeline()
                val generationStatusResponse = generateImage(pipeline[0].id, prompt)
                generationStatusResponse?.first()?.let { file ->
                    val bitmap = displayBase64Image(file)
                    _uiState.update { bitmap }
                }
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
}