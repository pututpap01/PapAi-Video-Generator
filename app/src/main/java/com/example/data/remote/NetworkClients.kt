package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiContentRequest
    ): GeminiGenerateResponse

    @POST("v1beta/models/{model}:generateVideos")
    suspend fun generateVideos(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: VeoVideoRequest
    ): ResponseBody

    @GET("v1beta/{operationName}")
    suspend fun getOperation(
        @Path("operationName", encoded = true) operationName: String,
        @Query("key") apiKey: String
    ): ResponseBody
}

interface ModalFastApiService {
    @POST
    suspend fun generateHunyuanVideo(
        @Url fullUrl: String,
        @Header("Authorization") authHeader: String?,
        @Body request: ModalHunyuanVideoRequest
    ): ModalHunyuanVideoResponse

    @GET
    suspend fun checkHealth(
        @Url healthUrl: String,
        @Header("Authorization") authHeader: String?
    ): ResponseBody
}

object NetworkClientProvider {
    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val geminiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    val modalService: ModalFastApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://modal.run/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ModalFastApiService::class.java)
    }
}
