package com.example.data.model

enum class AspectRatio(
    val title: String,
    val ratioLabel: String,
    val widthRatio: Float,
    val heightRatio: Float,
    val apiParam: String
) {
    PORTRAIT_9_16("Portrait (Shorts/Reels)", "9:16", 9f, 16f, "9:16"),
    LANDSCAPE_16_9("Landscape (Cinema/YT)", "16:9", 16f, 9f, "16:9"),
    SQUARE_1_1("Square (Feed/Post)", "1:1", 1f, 1f, "1:1"),
    CLASSIC_4_3("Classic TV", "4:3", 4f, 3f, "4:3"),
    ULTRAWIDE_21_9("Ultrawide Movie", "21:9", 21f, 9f, "21:9")
}

enum class VideoEngine(
    val displayName: String,
    val badge: String,
    val description: String,
    val isDitArchitecture: Boolean
) {
    HUNYUAN_MODAL(
        "HunyuanVideo (Modal.com)",
        "DiT Open-Weights",
        "Diffusion Transformer backend on Modal GPU with realistic gravity & cloth folds",
        true
    ),
    VEO_3_FAST(
        "Google Veo 3.1 Fast",
        "Veo-3.1",
        "High-speed realistic human video generation via Google AI",
        false
    ),
    WAN_2_1_DIT(
        "Wan 2.1 DiT",
        "Open-Weights",
        "Temporal Diffusion Transformer with photorealistic micro-expressions",
        true
    )
}

enum class MotionStyle(
    val title: String,
    val tag: String,
    val iconName: String,
    val promptModifier: String
) {
    HYPER_REALISTIC(
        "Hyper-Realistic Physics",
        "Physics",
        "flash_on",
        "photorealistic 8k, real world physics, authentic natural cloth wrinkles and fabric folds, realistic gravity, subtle muscle twitch and facial micro-expressions, 60fps smooth kinematics"
    ),
    CINEMATIC_MASTER(
        "Cinematic Master",
        "Cinema",
        "movie",
        "35mm anamorphic lens, shallow depth of field, natural motion blur, cinematic volumetric lighting, film grain, dramatic composition"
    ),
    STUDIO_FASHION(
        "Studio Fashion & Fabric",
        "Fashion",
        "checkroom",
        "high fashion editorial studio, high-speed camera, dynamic silk cloth simulation flowing in wind, crisp rim lighting, sharp human features"
    ),
    DYNAMIC_ATHLETIC(
        "Dynamic Athletic",
        "Action",
        "sports",
        "explosive human movement, accurate biomechanics, muscle definition, dynamic camera tracking, high speed shutter"
    ),
    STREET_AUTHENTIC(
        "Street Life Motion",
        "Realism",
        "directions_walk",
        "candid documentary footage, natural urban lighting, authentic walking motion with natural weight shifts, natural clothes reaction"
    ),
    SLOW_MO_ELEGANCE(
        "Slow-Mo Elegance",
        "120 FPS",
        "slow_motion_video",
        "120fps high frame rate slow motion, graceful human motion, liquid-like fabric flow, micro facial expressions, crystal clear details"
    ),
    CYBER_NEON(
        "Cyberpunk Neon",
        "Stylized",
        "auto_awesome",
        "neon reflections, rain drops reacting to movement, futuristic glowing accents, realistic human motion in dystopian night setting"
    )
}

enum class CameraMovement(
    val title: String,
    val description: String,
    val promptText: String
) {
    STATIC_TRIPOD("Static Tripod", "Fixed frame focusing purely on human movement", "static camera, fixed focal length, sharp focus on subject"),
    ORBIT_360("Cinematic Orbit", "Smooth 360 camera rotation around the subject", "smooth circular orbit camera movement circling around subject"),
    DOLLY_ZOOM("Dolly Zoom In", "Camera pushes forward dynamically", "slow cinematic dolly in pushing towards subject"),
    SMOOTH_PAN("Tracking Pan", "Camera pans horizontally tracking motion", "horizontal tracking pan following human movement smoothly"),
    HANDHELD_SHAKY("Handheld Realistic", "Natural subtle camera shake for realism", "organic handheld camera with subtle natural breathing shake")
}

data class PhysicsSettings(
    val gravityStrength: Float = 9.8f, // m/s^2
    val clothFoldFidelity: Float = 0.85f, // 0..1
    val facialMicroExpression: Float = 0.90f, // 0..1
    val inertiaSimulation: Float = 0.80f, // 0..1
    val cameraMovement: CameraMovement = CameraMovement.DOLLY_ZOOM
)

data class ReferenceImageItem(
    val id: String,
    val name: String,
    val drawableRes: Int? = null,
    val uriString: String? = null,
    val poseDescription: String = ""
)
