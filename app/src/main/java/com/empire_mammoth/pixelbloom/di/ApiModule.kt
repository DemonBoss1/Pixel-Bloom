package com.empire_mammoth.pixelbloom.di

import dagger.Provides
import okhttp3.Interceptor
import javax.inject.Singleton

class ApiModule {
    @Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("X-Key", "Key $apiKey")
                .header("X-Secret", "Secret $secretKey")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }
}