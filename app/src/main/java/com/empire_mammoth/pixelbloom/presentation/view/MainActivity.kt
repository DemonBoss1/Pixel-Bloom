package com.empire_mammoth.pixelbloom.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.empire_mammoth.pixelbloom.R
import com.empire_mammoth.pixelbloom.data.api.GenerateApiService
import com.empire_mammoth.pixelbloom.databinding.ActivityMainBinding
import com.empire_mammoth.pixelbloom.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var binding: ActivityMainBinding? = null

    @Inject
    lateinit var generateApiService: GenerateApiService

    private val defaultBitmap: Bitmap by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.error_image)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbar)

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
                viewModel.uiState.collect { state ->
                    binding?.apply {
                        sendButton.isEnabled = true
                        progressBar.visibility = View.GONE
                        if (state.isError)
                            imageViewMain.setImageBitmap(defaultBitmap)
                        else
                            imageViewMain.setImageBitmap(state.bitmap)
                        imageViewMain.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                viewModel.saveData()
                true
            }
            else -> super.onOptionsItemSelected(item)
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