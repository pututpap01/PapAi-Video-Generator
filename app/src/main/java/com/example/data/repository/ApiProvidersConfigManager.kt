package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences

data class ProvidersConfig(
    val replicateToken: String,
    val falAiKey: String,
    val geminiVeoKey: String,
    val modalEndpoint: String,
    val modalToken: String,
    val preferredResolution: String, // "720p", "1080p", "4k"
    val isModalEnabled: Boolean
)

class ApiProvidersConfigManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pap_api_providers_config", Context.MODE_PRIVATE)

    companion object {
        const val DEFAULT_REPLICATE_TOKEN = ""
        const val DEFAULT_FAL_KEY = ""
        const val DEFAULT_MODAL_ENDPOINT = "https://pututadif--hunyuanvideo-fastapi-fastapi-app.modal.run/generate"
        const val DEFAULT_MODAL_TOKEN = "ak-hdnamdluuWZBMYTB2rLghU:as-BvBUZx69tj3wSgpji9iM2b"
    }

    fun getConfig(): ProvidersConfig {
        return ProvidersConfig(
            replicateToken = prefs.getString("replicate_token", DEFAULT_REPLICATE_TOKEN) ?: DEFAULT_REPLICATE_TOKEN,
            falAiKey = prefs.getString("fal_ai_key", DEFAULT_FAL_KEY) ?: DEFAULT_FAL_KEY,
            geminiVeoKey = prefs.getString("gemini_veo_key", "") ?: "",
            modalEndpoint = prefs.getString("modal_endpoint", DEFAULT_MODAL_ENDPOINT) ?: DEFAULT_MODAL_ENDPOINT,
            modalToken = prefs.getString("modal_token", DEFAULT_MODAL_TOKEN) ?: DEFAULT_MODAL_TOKEN,
            preferredResolution = prefs.getString("preferred_resolution", "1080p") ?: "1080p",
            isModalEnabled = prefs.getBoolean("modal_enabled", true)
        )
    }

    fun saveConfig(config: ProvidersConfig) {
        prefs.edit()
            .putString("replicate_token", config.replicateToken.trim())
            .putString("fal_ai_key", config.falAiKey.trim())
            .putString("gemini_veo_key", config.geminiVeoKey.trim())
            .putString("modal_endpoint", config.modalEndpoint.trim())
            .putString("modal_token", config.modalToken.trim())
            .putString("preferred_resolution", config.preferredResolution)
            .putBoolean("modal_enabled", config.isModalEnabled)
            .apply()
    }
}
