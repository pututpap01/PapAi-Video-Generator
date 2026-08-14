package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiContentRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null,
    val inlineData: GeminiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerateResponse(
    val candidates: List<GeminiCandidate>? = null,
    val error: GeminiError? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)

// Veo 3.1 Video Generation Data Classes
@JsonClass(generateAdapter = true)
data class VeoVideoRequest(
    val prompt: String,
    val config: VeoVideoConfig? = null
)

@JsonClass(generateAdapter = true)
data class VeoVideoConfig(
    val numberOfVideos: Int = 1,
    val resolution: String = "720p",
    val aspectRatio: String = "16:9",
    val durationSeconds: Int = 5
)

@JsonClass(generateAdapter = true)
data class VeoOperationResponse(
    val name: String? = null,
    val done: Boolean? = false,
    val response: Map<String, Any>? = null,
    val error: GeminiError? = null
)
