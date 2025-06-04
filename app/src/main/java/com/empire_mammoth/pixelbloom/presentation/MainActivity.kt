package com.empire_mammoth.pixelbloom.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.empire_mammoth.pixelbloom.R
import com.empire_mammoth.pixelbloom.data.GenerateApiService
import com.empire_mammoth.pixelbloom.databinding.ActivityMainBinding
import com.empire_mammoth.pixelbloom.di.DaggerAppComponent
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

        val data = generateApiService.getPipeline()
            //binding?.textViewMain?.text = data.body().toString()

    }
}