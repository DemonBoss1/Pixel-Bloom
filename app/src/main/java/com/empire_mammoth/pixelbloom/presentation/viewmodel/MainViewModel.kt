package com.empire_mammoth.pixelbloom.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empire_mammoth.pixelbloom.data.model.Pipeline
import com.empire_mammoth.pixelbloom.domain.model.SaveStatus
import com.empire_mammoth.pixelbloom.domain.usecase.DecodeImageUseCase
import com.empire_mammoth.pixelbloom.domain.usecase.GetGenerateImageUseCase
import com.empire_mammoth.pixelbloom.domain.usecase.GetPipelineUseCase
import com.empire_mammoth.pixelbloom.domain.usecase.SaveImageUseCase
import com.empire_mammoth.pixelbloom.presentation.model.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPipelineUseCase: GetPipelineUseCase,
    private val getGenerateImageUseCase: GetGenerateImageUseCase,
    private val decodeImageUseCase: DecodeImageUseCase,
    private val saveImageUseCase: SaveImageUseCase
) : ViewModel() {

    private var pipeline: Pipeline? =null
    private var idRequest = 0
    private val _uiState = MutableStateFlow(ViewState(idRequest, null, false))
    val uiState: StateFlow<ViewState> = _uiState

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus

    fun generate(prompt: String) {
        idRequest++
        viewModelScope.launch {
            try {
                pipeline = if(pipeline==null) getPipelineUseCase() else pipeline
                val generationStatusResponse = pipeline?.let { getGenerateImageUseCase(it.id, prompt) }
                val bitmap = generationStatusResponse?.firstOrNull()?.let { base64String ->
                    decodeImageUseCase(base64String)
                }
                _uiState.update { ViewState(idRequest, bitmap, bitmap == null) }
            } catch (e: Exception) {
                val error = e.message
            }
        }
    }

    fun saveData() {
        _uiState.value.bitmap?.let { saveImage(it) }
    }

    private fun saveImage(bitmap: Bitmap) = viewModelScope.launch {
        _saveStatus.value = SaveStatus.Loading
        viewModelScope.launch {
            saveImageUseCase(bitmap)
        }
    }
}