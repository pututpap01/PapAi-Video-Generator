package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GenerationUiState
import com.example.ui.viewmodel.VideoGeneratorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: VideoGeneratorViewModel,
    onOpenHistory: () -> Unit,
    onOpenModalSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prompt by viewModel.prompt.collectAsState()
    val enhancedPrompt by viewModel.enhancedPrompt.collectAsState()
    val isEnhancing by viewModel.isEnhancingPrompt.collectAsState()

    val selectedRatio by viewModel.selectedAspectRatio.collectAsState()
    val selectedDuration by viewModel.selectedDuration.collectAsState()
    val selectedEngine by viewModel.selectedEngine.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val physicsSettings by viewModel.physicsSettings.collectAsState()

    val referenceImageUri by viewModel.referenceImageUri.collectAsState()
    val referencePoseAnalysis by viewModel.referencePoseAnalysis.collectAsState()
    val isAnalyzingImage by viewModel.isAnalyzingImage.collectAsState()

    val generationState by viewModel.generationUiState.collectAsState()
    val currentPlayingProject by viewModel.currentPlayingProject.collectAsState()
    val savedProjects by viewModel.savedProjects.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(CyanGlow, VioletNeon))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PAP AI GENERATOR",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = "Realistic Human Motion Video • HunyuanVideo DiT & Veo 3",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyanGlow,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    // History Icon with badge
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.testTag("button_open_history")
                    ) {
                        BadgedBox(
                            badge = {
                                if (savedProjects.isNotEmpty()) {
                                    Badge(
                                        containerColor = CyanGlow,
                                        contentColor = Color.Black
                                    ) {
                                        Text("${savedProjects.size}", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = TextPrimary
                            )
                        }
                    }

                    // Modal Config button
                    IconButton(
                        onClick = onOpenModalSettings,
                        modifier = Modifier.testTag("button_open_modal_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Modal Settings",
                            tint = VioletNeon
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = BackgroundDark,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Active Video Preview (if generated / selected)
            if (currentPlayingProject != null) {
                VideoPlayerView(
                    project = currentPlayingProject!!,
                    onRegenerate = { viewModel.generateVideo() }
                )
            }

            // 2. Generation Progress HUD (if currently rendering)
            if (generationState is GenerationUiState.Generating) {
                val state = generationState as GenerationUiState.Generating
                GenerationProgressCard(
                    step = state.step,
                    totalSteps = state.totalSteps,
                    message = state.message,
                    progress = state.progress,
                    onCancel = { viewModel.cancelGeneration() }
                )
            }

            // 3. Prompt Input & AI Enhancer
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = CyanGlow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Instruksi Gerakan Manusia",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        Button(
                            onClick = { viewModel.enhancePromptWithAI() },
                            enabled = !isEnhancing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VioletNeon.copy(alpha = 0.2f),
                                contentColor = VioletNeon
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("button_enhance_prompt")
                        ) {
                            if (isEnhancing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = VioletNeon
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Menganalisis AI...",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VioletNeon
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = VioletNeon,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI Enhance DiT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VioletNeon
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.updatePrompt(it) },
                        placeholder = { Text("Contoh: Seorang penari berputar dengan gaun sutra di bawah gravitasi alami...") },
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanGlow,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_prompt")
                    )

                    // Quick Prompt Inspiration Chips
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val samplePrompts = listOf(
                            "Penari Sutra" to "Penari wanita bergerak anggun dengan gaun sutra mengalir dinamis, gravitasi natural dan lipatan kain halus",
                            "Pelari Sprint" to "Pelari atletik melakukan start sprint cepat, kontraksi otot realistis dan debu terangkat",
                            "Model Catwalk" to "Model profesional berjalan di runway fashion, kain satin berkilau dan langkah mantap"
                        )
                        samplePrompts.forEach { (label, text) ->
                            SuggestionChip(
                                onClick = { viewModel.updatePrompt(text) },
                                label = { Text(label, fontSize = 10.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = SurfaceCardElevated,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }

            // 4. Reference Image & Gemini Vision Analysis
            ImageReferencePicker(
                selectedImageUri = referenceImageUri,
                poseAnalysis = referencePoseAnalysis,
                isAnalyzing = isAnalyzingImage,
                onImageSelected = { viewModel.setReferenceImage(it) },
                onAnalyzeImage = { viewModel.analyzeReference(it) }
            )

            // 5. Aspect Ratio Selector (Dropdown Scrolldown)
            AspectPicker(
                selectedRatio = selectedRatio,
                onRatioSelected = { viewModel.setAspectRatio(it) }
            )

            // 6. Style & Visual Filters
            StyleFilterRow(
                selectedStyle = selectedStyle,
                onStyleSelected = { viewModel.setStyle(it) }
            )

            // 7. Video Duration Selector
            DurationPicker(
                selectedDuration = selectedDuration,
                onDurationSelected = { viewModel.setDuration(it) }
            )

            // 8. Engine Selector (Modal.com HunyuanVideo DiT / Veo 3.1 Fast)
            EngineSelector(
                selectedEngine = selectedEngine,
                onEngineSelected = { viewModel.setEngine(it) },
                onOpenModalSettings = onOpenModalSettings
            )

            // 9. Real-World Physics Settings (Gravity, Cloth Folds, Facial Wrinkles, Camera Motion)
            PhysicsSettingsCard(
                physicsSettings = physicsSettings,
                onPhysicsChanged = { viewModel.updatePhysics(it) }
            )

            // 10. Action Button: Generate Video
            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { viewModel.generateVideo() },
                enabled = generationState !is GenerationUiState.Generating,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(CyanGlow, VioletNeon, CoralAccent)
                        )
                    )
                    .testTag("button_generate_video")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoCall,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HASILKAN VIDEO GERAKAN MANUSIA",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
