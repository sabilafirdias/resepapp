package com.example.resepappy.repositori

import android.app.Application
import com.example.resepappy.apiService.ApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

interface ContainerApp {
    val repositoryResep: ResepRepository
}

class DefaultContainerApp : ContainerApp {
    private val baseUrl = "http://10.0.2.2/resepappy/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            }.asConverterFactory("application/json".toMediaType())
        )
        .build()

    private val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    override val repositoryResep: ResepRepository by lazy {
        JaringanResepRepository(retrofitService)
    }
}

class AplikasiResep : Application() {

    lateinit var container: ContainerApp

    override fun onCreate() {
        super.onCreate()
        this.container = DefaultContainerApp()
    }
}