package com.empire_mammoth.pixelbloom.domain.model

import android.net.Uri

sealed class SaveStatus {
    object Idle : SaveStatus()
    object Loading : SaveStatus()
    data class Success(val uri: Uri) : SaveStatus()
    data class Error(val message: String) : SaveStatus()
}
