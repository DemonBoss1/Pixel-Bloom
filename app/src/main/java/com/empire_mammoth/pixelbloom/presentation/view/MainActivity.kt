package com.empire_mammoth.pixelbloom.presentation.view

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.empire_mammoth.pixelbloom.data.api.GenerateApiService
import com.empire_mammoth.pixelbloom.databinding.ActivityMainBinding
import com.empire_mammoth.pixelbloom.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var binding: ActivityMainBinding? = null

    @Inject
    lateinit var generateApiService: GenerateApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {
            sendButton.setOnClickListener {
                sendButton.isEnabled = false
                imageViewMain.visibility = View.GONE
                progressBar.visibility = View.VISIBLE

                clearFocus()

                val prompt = editTextPrompt.text.toString()
                viewModel.generate(prompt)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { bitmap ->
                    binding?.apply {
                        sendButton.isEnabled = true
                        progressBar.visibility = View.GONE
                        imageViewMain.setImageBitmap(bitmap)
                        imageViewMain.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun clearFocus() {
        binding?.apply {
            editTextPrompt.clearFocus()
            val imm =
                baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editTextPrompt.windowToken, 0)
        }
    }
}