package com.empire_mammoth.pixelbloom.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.empire_mammoth.pixelbloom.data.api.GenerateApiService
import com.empire_mammoth.pixelbloom.databinding.ActivityMainBinding
import com.empire_mammoth.pixelbloom.di.DaggerAppComponent
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? =null

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
                runOnUiThread {
                    binding?.textViewMain?.text = pipeline[0].toString()
                }
                // Работа с pipeline
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }

    }
}