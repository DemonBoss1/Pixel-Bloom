package com.empire_mammoth.pixelbloom.presentation

import android.os.Bundle
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

        lifecycleScope.launch {
            try {
                val pipeline = generateApiService.getPipeline()
                val generationStatusResponse = generateImage(pipeline[0].id, "Sun in sky")
                runOnUiThread {
                    binding?.textViewMain?.text = generationStatusResponse?.first()
                }
            } catch (e: Exception) {
                // Обработка ошибки
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
        while(attempts>0){
            val status = generateApiService.getGenerationStatus(generationStatusResponse.uuid)
            if(status.status == "DONE") return status.result?.files
            attempts -= 1
            delay(5000)
        }
        return null
    }
}