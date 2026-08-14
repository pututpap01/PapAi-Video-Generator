package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences

data class ModalConfig(
    val endpointUrl: String,
    val apiToken: String,
    val gpuType: String,
    val isCustomServerEnabled: Boolean
)

class ModalConfigManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pap_modal_config", Context.MODE_PRIVATE)

    companion object {
        const val DEFAULT_ENDPOINT = "https://pututadif--hunyuanvideo-fastapi-fastapi-app.modal.run/generate"
        const val DEFAULT_TOKEN = "ak-hdnamdluuWZBMYTB2rLghU:as-BvBUZx69tj3wSgpji9iM2b"
        const val DEFAULT_GPU = "NVIDIA H100 (80GB SXM5)"
    }

    fun getConfig(): ModalConfig {
        return ModalConfig(
            endpointUrl = prefs.getString("endpoint_url", DEFAULT_ENDPOINT) ?: DEFAULT_ENDPOINT,
            apiToken = prefs.getString("api_token", DEFAULT_TOKEN) ?: DEFAULT_TOKEN,
            gpuType = prefs.getString("gpu_type", DEFAULT_GPU) ?: DEFAULT_GPU,
            isCustomServerEnabled = prefs.getBoolean("custom_server_enabled", true)
        )
    }

    fun saveConfig(config: ModalConfig) {
        prefs.edit()
            .putString("endpoint_url", config.endpointUrl)
            .putString("api_token", config.apiToken)
            .putString("gpu_type", config.gpuType)
            .putBoolean("custom_server_enabled", config.isCustomServerEnabled)
            .apply()
    }

    fun getPythonDeploymentCode(): String {
        return """
# ===============================================================
# Modal.com + HunyuanVideo (Diffusion Transformer - DiT) Backend
# GitHub Open-Source Blueprint with diffusers & FastAPI
# ===============================================================
import modal
import io
import base64
import torch
from fastapi import FastAPI, Header, HTTPException
from pydantic import BaseModel

app = modal.App("hunyuanvideo-fastapi")

# Configure GPU Image with Diffusers and HunyuanVideo Dependencies
image = (
    modal.Image.debian_slim(python_version="3.11")
    .pip_install(
        "torch==2.4.0",
        "diffusers>=0.31.0",
        "transformers>=4.44.0",
        "accelerate>=0.33.0",
        "sentencepiece",
        "imageio[ffmpeg]",
        "fastapi[standard]",
        "pydantic"
    )
)

web_app = FastAPI(title="PAP AI HunyuanVideo DiT API")

class VideoGenRequest(BaseModel):
    prompt: String
    aspect_ratio: str = "16:9"
    duration_sec: int = 5
    gravity_strength: float = 9.8
    cloth_folds_fidelity: float = 0.85
    facial_wrinkles_fidelity: float = 0.90
    camera_movement: str = "dolly_zoom"
    reference_image_base64: str | None = None

@app.cls(gpu="H100", image=image, container_idle_timeout=120)
class HunyuanVideoModel:
    @modal.enter()
    def load_model(self):
        from diffusers import HunyuanVideoPipeline, HunyuanVideoTransformer3DModel
        print("Loading HunyuanVideo DiT weights onto GPU...")
        self.pipe = HunyuanVideoPipeline.from_pretrained(
            "tencent/HunyuanVideo",
            torch_dtype=torch.bfloat16
        ).to("cuda")
        self.pipe.vae.enable_tiling()

    @modal.method()
    def generate(self, req: VideoGenRequest) -> dict:
        # Construct physics-aware prompt conditioning
        physics_prompt = (
            f"{req.prompt}, real-world gravity {req.gravity_strength}m/s2, "
            f"natural cloth folds and wrinkle physics {req.cloth_folds_fidelity*100}%, "
            f"facial micro-wrinkles fidelity {req.facial_wrinkles_fidelity*100}%, "
            f"camera: {req.camera_movement}"
        )
        
        frames = self.pipe(
            prompt=physics_prompt,
            num_frames=int(req.duration_sec * 24),
            height=720 if req.aspect_ratio == "16:9" else 1280,
            width=1280 if req.aspect_ratio == "16:9" else 720,
            num_inference_steps=30,
            guidance_scale=6.0
        ).frames[0]
        
        # Encode video to base64 MP4
        # ... export video buffer ...
        return {
            "status": "success",
            "model_engine": "HunyuanVideo-DiT-Modal",
            "gpu_device": "NVIDIA-H100-SXM5"
        }

@app.function(image=image)
@modal.asgi_app()
def fastapi_app():
    return web_app
""".trimIndent()
    }
}
