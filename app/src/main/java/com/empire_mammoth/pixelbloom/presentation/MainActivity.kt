package com.empire_mammoth.pixelbloom.presentation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.empire_mammoth.pixelbloom.data.api.GenerateApiService
import com.empire_mammoth.pixelbloom.data.model.GenerationStatusResponse
import com.empire_mammoth.pixelbloom.databinding.ActivityMainBinding
import com.empire_mammoth.pixelbloom.di.DaggerAppComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    @Inject
    lateinit var generateApiService: GenerateApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val appComponent = DaggerAppComponent.create()
        appComponent.inject(this)

        binding?.apply {
            sendButton.setOnClickListener {
                sendButton.isEnabled = false
                imageViewMain.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    try {
                        val pipeline = generateApiService.getPipeline()
                        val prompt = editTextPrompt.text.toString()
                        val generationStatusResponse = generateImage(pipeline[0].id, prompt)
                        runOnUiThread {
                            binding?.apply {
                                sendButton.isEnabled = true
                                progressBar.visibility = View.GONE
                                imageViewMain.visibility = View.VISIBLE

                                generationStatusResponse?.first()?.let { file ->
                                    imageViewMain.visibility = View.VISIBLE
                                    displayBase64Image(file, imageViewMain)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        binding?.imageViewMain?.visibility = View.VISIBLE
                    }
                }
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

    private fun displayBase64Image(base64String: String, imageView: ImageView) {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            imageView.setImageBitmap(bitmap)

        } catch (e: IllegalArgumentException) {
            println("Error decoding Base64 string: ${e.message}")
            e.printStackTrace()
        } catch (e: Exception) {
            println("Error displaying image: ${e.message}")
            e.printStackTrace()
        }
    }
}