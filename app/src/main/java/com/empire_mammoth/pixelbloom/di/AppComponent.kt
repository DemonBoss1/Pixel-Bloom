package com.empire_mammoth.pixelbloom.di

import com.empire_mammoth.pixelbloom.presentation.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
}