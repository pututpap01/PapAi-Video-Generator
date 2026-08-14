package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModalHunyuanVideoRequest(
    val prompt: String,
    @Json(name = "aspect_ratio") val aspectRatio: String = "16:9",
    @Json(name = "duration_sec") val durationSec: Int = 5,
    @Json(name = "num_inference_steps") val numInferenceSteps: Int = 30,
    @Json(name = "guidance_scale") val guidanceScale: Float = 6.0f,
    @Json(name = "gravity_strength") val gravityStrength: Float = 9.8f,
    @Json(name = "cloth_folds_fidelity") val clothFoldsFidelity: Float = 0.85f,
    @Json(name = "facial_wrinkles_fidelity") val facialWrinklesFidelity: Float = 0.90f,
    @Json(name = "camera_movement") val cameraMovement: String = "dolly_zoom",
    @Json(name = "reference_image_base64") val referenceImageBase64: String? = null,
    @Json(name = "model_architecture") val modelArchitecture: String = "DiffusionTransformer_HunyuanVideo"
)

@JsonClass(generateAdapter = true)
data class ModalHunyuanVideoResponse(
    val status: String = "success",
    @Json(name = "video_url") val videoUrl: String? = null,
    @Json(name = "video_base64") val videoBase64: String? = null,
    @Json(name = "preview_thumbnail_url") val previewThumbnailUrl: String? = null,
    @Json(name = "inference_time_sec") val inferenceTimeSec: Float? = null,
    @Json(name = "model_engine") val modelEngine: String? = "HunyuanVideo-DiT-Modal",
    @Json(name = "gpu_device") val gpuDevice: String? = "NVIDIA-H100-SXM",
    val message: String? = null
)
