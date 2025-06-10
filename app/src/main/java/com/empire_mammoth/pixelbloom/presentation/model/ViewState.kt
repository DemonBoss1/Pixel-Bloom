package com.empire_mammoth.pixelbloom.presentation.model

import android.graphics.Bitmap

data class ViewState(
    val idRequest: Int,
    val bitmap: Bitmap?,
    val isError: Boolean
)