package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FalAiQueueResponse(
    @Json(name = "request_id") val requestId: String,
    val status: String? = null,
    @Json(name = "response_url") val responseUrl: String? = null,
    @Json(name = "status_url") val statusUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class FalAiStatusResponse(
    val status: String, // "IN_QUEUE", "IN_PROGRESS", "COMPLETED", "FAILED"
    @Json(name = "queue_position") val queuePosition: Int? = null,
    val response: FalAiVideoResult? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class FalAiVideoResult(
    val video: FalAiVideoFile? = null,
    val seed: Long? = null
)

@JsonClass(generateAdapter = true)
data class FalAiVideoFile(
    val url: String,
    @Json(name = "content_type") val contentType: String? = null,
    @Json(name = "file_name") val fileName: String? = null,
    @Json(name = "file_size") val fileSize: Long? = null
)
