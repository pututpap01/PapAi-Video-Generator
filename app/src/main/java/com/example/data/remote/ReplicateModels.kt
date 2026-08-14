package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReplicatePredictionRequest(
    val version: String? = null,
    val input: Map<String, Any>
)

@JsonClass(generateAdapter = true)
data class ReplicatePredictionResponse(
    val id: String,
    val status: String, // "starting", "processing", "succeeded", "failed", "canceled"
    val output: Any? = null, // Can be String URL or List<String>
    val error: String? = null,
    val logs: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    val urls: ReplicateUrls? = null
)

@JsonClass(generateAdapter = true)
data class ReplicateUrls(
    val get: String? = null,
    val cancel: String? = null
)
