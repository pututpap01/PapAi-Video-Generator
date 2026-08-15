package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.VideoProjectEntity
import com.example.data.model.*
import com.example.data.repository.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class GenerationUiState {
    object Idle : GenerationUiState()
    data class Generating(
        val step: Int,
        val totalSteps: Int,
        val message: String,
        val progress: Float
    ) : GenerationUiState()
    data class Success(
        val project: VideoProjectEntity,
        val summary: String
    ) : GenerationUiState()
    data class Error(val message: String) : GenerationUiState()
}

class VideoGeneratorViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val projectRepo = ProjectRepository(database.videoProjectDao())
    val modalConfigManager = ModalConfigManager(application)
    val providersConfigManager = ApiProvidersConfigManager(application)
    private val videoRepo = VideoGenerationRepository(application, modalConfigManager, providersConfigManager)

    // User inputs
    var prompt = MutableStateFlow("Seorang penari dalam gaun sutra mengalir bergerak anggun di bawah gravitasi alami dengan lipatan kain dinamis")
    var enhancedPrompt = MutableStateFlow("")
    var isEnhancingPrompt = MutableStateFlow(false)

    var selectedAspectRatio = MutableStateFlow(AspectRatio.PORTRAIT_9_16)
    var selectedDuration = MutableStateFlow(5)
    var selectedEngine = MutableStateFlow(VideoEngine.REPLICATE_HUNYUAN)
    var selectedStyle = MutableStateFlow(MotionStyle.HYPER_REALISTIC)
    var physicsSettings = MutableStateFlow(PhysicsSettings())

    var referenceImageUri = MutableStateFlow<Uri?>(null)
    var referencePoseAnalysis = MutableStateFlow<String?>(null)
    var isAnalyzingImage = MutableStateFlow(false)

    var generationUiState = MutableStateFlow<GenerationUiState>(GenerationUiState.Idle)
    var currentPlayingProject = MutableStateFlow<VideoProjectEntity?>(null)
    var selectedTab = MutableStateFlow(0) // 0: Generator, 1: Projects History, 2: Modal Backend Settings

    val savedProjects: StateFlow<List<VideoProjectEntity>> = projectRepo.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var generationJob: Job? = null

    init {
        // Build initial enhanced prompt
        updateEnhancedPromptPreview()
    }

    fun updatePrompt(newPrompt: String) {
        prompt.value = newPrompt
        updateEnhancedPromptPreview()
    }

    fun setAspectRatio(ratio: AspectRatio) {
        selectedAspectRatio.value = ratio
    }

    fun setDuration(duration: Int) {
        selectedDuration.value = duration
    }

    fun setEngine(engine: VideoEngine) {
        selectedEngine.value = engine
    }

    fun setStyle(style: MotionStyle) {
        selectedStyle.value = style
        updateEnhancedPromptPreview()
    }

    fun updatePhysics(newPhysics: PhysicsSettings) {
        physicsSettings.value = newPhysics
        updateEnhancedPromptPreview()
    }

    fun setReferenceImage(uri: Uri?) {
        referenceImageUri.value = uri
        if (uri != null) {
            analyzeReference(uri)
        } else {
            referencePoseAnalysis.value = null
            updateEnhancedPromptPreview()
        }
    }

    fun analyzeReference(uri: Uri) {
        viewModelScope.launch {
            isAnalyzingImage.value = true
            val result = videoRepo.analyzeReferenceImage(uri)
            result.onSuccess { analysis ->
                referencePoseAnalysis.value = analysis
                updateEnhancedPromptPreview()
            }
            isAnalyzingImage.value = false
        }
    }

    fun enhancePromptWithAI() {
        viewModelScope.launch {
            isEnhancingPrompt.value = true
            val currentRaw = prompt.value.ifBlank { "Seorang penari dalam gaun sutra mengalir bergerak anggun di bawah gravitasi alami dengan lipatan kain dinamis" }
            val result = videoRepo.enhancePrompt(
                rawPrompt = currentRaw,
                style = selectedStyle.value,
                physics = physicsSettings.value,
                refPoseAnalysis = referencePoseAnalysis.value
            )
            prompt.value = result
            enhancedPrompt.value = result
            isEnhancingPrompt.value = false
        }
    }

    private fun updateEnhancedPromptPreview() {
        if (!isEnhancingPrompt.value) {
            val style = selectedStyle.value
            val physics = physicsSettings.value
            val pose = referencePoseAnalysis.value
            val p = prompt.value.ifBlank { "Realistic human motion sequence" }
            val posePart = if (!pose.isNullOrBlank()) " [Ref Pose: ${pose.take(45)}...]" else ""
            enhancedPrompt.value = "$p$posePart, ${style.promptModifier}, Gravity: ${physics.gravityStrength}m/s², Cloth Folds: ${(physics.clothFoldFidelity * 100).toInt()}%, Facial Wrinkles: ${(physics.facialMicroExpression * 100).toInt()}%, Camera: ${physics.cameraMovement.title}."
        }
    }

    fun generateVideo() {
        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            val currentPrompt = prompt.value
            val currentEnhanced = enhancedPrompt.value.ifBlank { currentPrompt }
            val ratio = selectedAspectRatio.value
            val duration = selectedDuration.value
            val engine = selectedEngine.value
            val style = selectedStyle.value
            val physics = physicsSettings.value
            val refUri = referenceImageUri.value?.toString()
            val refAnalysis = referencePoseAnalysis.value

            videoRepo.generateVideoFlow(
                prompt = currentPrompt,
                enhancedPrompt = currentEnhanced,
                aspectRatio = ratio,
                durationSeconds = duration,
                engine = engine,
                style = style,
                physics = physics,
                refImageUri = refUri,
                refPoseAnalysis = refAnalysis
            ).collect { progress ->
                when (progress) {
                    is GenerationProgress.Step -> {
                        generationUiState.value = GenerationUiState.Generating(
                            step = progress.stepIndex,
                            totalSteps = progress.totalSteps,
                            message = progress.message,
                            progress = progress.percent
                        )
                    }
                    is GenerationProgress.Success -> {
                        val newEntity = VideoProjectEntity(
                            prompt = currentPrompt,
                            enhancedPrompt = currentEnhanced,
                            aspectRatio = ratio.ratioLabel,
                            durationSeconds = duration,
                            engine = engine.displayName,
                            style = style.title,
                            cameraMovement = physics.cameraMovement.title,
                            referenceImageUri = refUri,
                            referencePoseAnalysis = refAnalysis,
                            videoUrl = progress.videoResultUrl,
                            localVideoPath = null,
                            previewThumbnailUrl = refUri,
                            gravity = physics.gravityStrength,
                            clothFoldFidelity = physics.clothFoldFidelity,
                            facialFidelity = physics.facialMicroExpression
                        )
                        val id = projectRepo.saveProject(newEntity)
                        val savedEntity = newEntity.copy(id = id)
                        currentPlayingProject.value = savedEntity
                        generationUiState.value = GenerationUiState.Success(
                            project = savedEntity,
                            summary = progress.summary
                        )
                    }
                    is GenerationProgress.Error -> {
                        generationUiState.value = GenerationUiState.Error(progress.errorMessage)
                    }
                }
            }
        }
    }

    fun cancelGeneration() {
        generationJob?.cancel()
        generationUiState.value = GenerationUiState.Idle
    }

    fun deleteProject(project: VideoProjectEntity) {
        viewModelScope.launch {
            projectRepo.deleteProject(project)
            if (currentPlayingProject.value?.id == project.id) {
                currentPlayingProject.value = null
            }
        }
    }

    fun selectProjectForPlayback(project: VideoProjectEntity) {
        currentPlayingProject.value = project
        selectedTab.value = 0
    }

    fun loadProjectIntoEditor(project: VideoProjectEntity) {
        prompt.value = project.prompt
        enhancedPrompt.value = project.enhancedPrompt
        selectedAspectRatio.value = AspectRatio.values().find { it.ratioLabel == project.aspectRatio } ?: AspectRatio.PORTRAIT_9_16
        selectedDuration.value = project.durationSeconds
        selectedEngine.value = VideoEngine.values().find { it.displayName == project.engine } ?: VideoEngine.HUNYUAN_MODAL
        selectedStyle.value = MotionStyle.values().find { it.title == project.style } ?: MotionStyle.HYPER_REALISTIC
        physicsSettings.value = PhysicsSettings(
            gravityStrength = project.gravity,
            clothFoldFidelity = project.clothFoldFidelity,
            facialMicroExpression = project.facialFidelity,
            cameraMovement = CameraMovement.values().find { it.title == project.cameraMovement } ?: CameraMovement.DOLLY_ZOOM
        )
        referenceImageUri.value = project.referenceImageUri?.let { Uri.parse(it) }
        referencePoseAnalysis.value = project.referencePoseAnalysis
        currentPlayingProject.value = project
        selectedTab.value = 0
    }
}
