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

interface ReplicateApiService {
    @POST("v1/predictions")
    suspend fun createPrediction(
        @Header("Authorization") authHeader: String,
        @Body request: ReplicatePredictionRequest
    ): ReplicatePredictionResponse

    @POST("v1/models/{model_owner}/{model_name}/predictions")
    suspend fun createModelPrediction(
        @Path("model_owner") modelOwner: String,
        @Path("model_name") modelName: String,
        @Header("Authorization") authHeader: String,
        @Body request: ReplicatePredictionRequest
    ): ReplicatePredictionResponse

    @GET("v1/predictions/{prediction_id}")
    suspend fun getPrediction(
        @Header("Authorization") authHeader: String,
        @Path("prediction_id") predictionId: String
    ): ReplicatePredictionResponse
}

interface FalAiApiService {
    @POST
    suspend fun submitQueue(
        @Url fullUrl: String,
        @Header("Authorization") authHeader: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): FalAiQueueResponse

    @GET
    suspend fun checkQueueStatus(
        @Url statusUrl: String,
        @Header("Authorization") authHeader: String
    ): FalAiStatusResponse

    @GET
    suspend fun getResult(
        @Url responseUrl: String,
        @Header("Authorization") authHeader: String
    ): FalAiVideoResult
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

    val replicateService: ReplicateApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.replicate.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ReplicateApiService::class.java)
    }

    val falAiService: FalAiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://queue.fal.run/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FalAiApiService::class.java)
    }
}
