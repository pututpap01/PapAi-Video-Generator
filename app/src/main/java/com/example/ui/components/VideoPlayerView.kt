package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.VideoProjectEntity
import com.example.data.model.AspectRatio
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VideoPlayerView(
    project: VideoProjectEntity,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var currentProgress by remember { mutableStateOf(0f) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }

    // Dynamic animation ticker for simulated realistic human motion & cloth wave
    val infiniteTransition = rememberInfiniteTransition(label = "motion_anim")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (2400 / playbackSpeed).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloth_wave"
    )

    val cameraPulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (1800 / playbackSpeed).toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cam_pulse"
    )

    // Aspect Ratio Calculation
    val aspectEnum = AspectRatio.values().find { it.ratioLabel == project.aspectRatio } ?: AspectRatio.PORTRAIT_9_16
    val aspectRatioFloat = aspectEnum.widthRatio / aspectEnum.heightRatio

    // Auto progress playback simulation
    LaunchedEffect(isPlaying, playbackSpeed, project.id) {
        if (isPlaying) {
            val stepTime = 50L
            val totalMillis = project.durationSeconds * 1000f
            while (true) {
                kotlinx.coroutines.delay(stepTime)
                if (isPlaying) {
                    currentProgress = (currentProgress + (stepTime * playbackSpeed / totalMillis)) % 1.0f
                }
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = BorderStroke(1.5.dp, CyanGlow.copy(alpha = 0.7f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Bar with Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyanGlow.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${project.aspectRatio} • ${project.durationSeconds}s",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VioletNeon.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = project.engine.take(18),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = VioletNeon,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Text(
                    text = "60 FPS • Real-World Physics",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Video Viewport Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp, max = 340.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundDark),
                contentAlignment = Alignment.Center
            ) {
                // Video Viewport with Exact Aspect Ratio Mask
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(aspectRatioFloat)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D1322))
                ) {
                    // Physics Motion Canvas: Rendering dynamic realistic human kinematic flow & cloth folds
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val centerX = w / 2f
                        val centerY = h / 2f

                        // Background radial glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(VioletNeon.copy(alpha = 0.3f), Color.Transparent),
                                center = Offset(centerX, centerY),
                                radius = w * 0.7f * (if (isPlaying) cameraPulse else 1f)
                            )
                        )

                        // Draw Grid lines simulating 3D physics space
                        for (i in 1..4) {
                            val yLine = h * (i / 5f)
                            drawLine(
                                color = BorderSubtle.copy(alpha = 0.35f),
                                start = Offset(0f, yLine),
                                end = Offset(w, yLine),
                                strokeWidth = 1f
                            )
                        }

                        // Flowing cloth folds & fabric ribbons
                        val clothFidelity = project.clothFoldFidelity
                        val clothPath1 = Path()
                        val clothPath2 = Path()
                        val segments = 40

                        clothPath1.moveTo(centerX - 30f, centerY - 60f)
                        clothPath2.moveTo(centerX + 30f, centerY - 60f)

                        for (i in 0..segments) {
                            val progressFactor = i / segments.toFloat()
                            val y = centerY - 60f + (progressFactor * (h * 0.45f))
                            val waveOffset1 = sin(wavePhase + progressFactor * 5f) * (24f * clothFidelity)
                            val waveOffset2 = cos(wavePhase + progressFactor * 4f) * (28f * clothFidelity)
                            val x1 = (centerX - 25f - (progressFactor * 30f)) + waveOffset1
                            val x2 = (centerX + 25f + (progressFactor * 30f)) + waveOffset2

                            clothPath1.lineTo(x1, y)
                            clothPath2.lineTo(x2, y)
                        }

                        drawPath(
                            path = clothPath1,
                            brush = Brush.verticalGradient(
                                listOf(CyanGlow.copy(alpha = 0.8f), VioletNeon.copy(alpha = 0.4f), Color.Transparent)
                            ),
                            style = Stroke(width = 4f, cap = StrokeCap.Round)
                        )

                        drawPath(
                            path = clothPath2,
                            brush = Brush.verticalGradient(
                                listOf(CoralAccent.copy(alpha = 0.8f), VioletNeon.copy(alpha = 0.4f), Color.Transparent)
                            ),
                            style = Stroke(width = 4f, cap = StrokeCap.Round)
                        )

                        // Human Motion Keypoints Kinematics (Head, Torso, Arms, Legs)
                        val headY = centerY - 90f + (sin(wavePhase) * 6f)
                        val torsoY = centerY - 30f + (sin(wavePhase * 0.9f) * 4f)
                        val leftHand = Offset(centerX - 55f + sin(wavePhase + 1f) * 20f, centerY - 20f + cos(wavePhase) * 15f)
                        val rightHand = Offset(centerX + 55f + cos(wavePhase + 1f) * 20f, centerY - 20f + sin(wavePhase) * 15f)
                        val leftFoot = Offset(centerX - 35f + sin(wavePhase) * 15f, centerY + 80f)
                        val rightFoot = Offset(centerX + 35f - sin(wavePhase) * 15f, centerY + 80f)

                        // Draw Limbs
                        drawLine(CyanGlow, Offset(centerX, headY), Offset(centerX, torsoY), strokeWidth = 5f, cap = StrokeCap.Round)
                        drawLine(CyanGlow, Offset(centerX, torsoY - 20f), leftHand, strokeWidth = 3.5f, cap = StrokeCap.Round)
                        drawLine(CyanGlow, Offset(centerX, torsoY - 20f), rightHand, strokeWidth = 3.5f, cap = StrokeCap.Round)
                        drawLine(VioletNeon, Offset(centerX, torsoY), leftFoot, strokeWidth = 4f, cap = StrokeCap.Round)
                        drawLine(VioletNeon, Offset(centerX, torsoY), rightFoot, strokeWidth = 4f, cap = StrokeCap.Round)

                        // Head & Eyes
                        drawCircle(Color.White, radius = 12f, center = Offset(centerX, headY))
                        drawCircle(CyanGlow, radius = 14f, center = Offset(centerX, headY), style = Stroke(2f))

                        // Joint indicators
                        val joints = listOf(leftHand, rightHand, leftFoot, rightFoot, Offset(centerX, torsoY))
                        joints.forEach { joint ->
                            drawCircle(AmberGlow, radius = 4f, center = joint)
                        }
                    }

                    // Watermark & Live HUD
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlaying) EmeraldGlow else CoralAccent)
                            )
                            Text(
                                text = "PAP AI • HunyuanDiT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrubber Bar
            Slider(
                value = currentProgress,
                onValueChange = {
                    currentProgress = it
                },
                colors = SliderDefaults.colors(
                    thumbColor = CyanGlow,
                    activeTrackColor = CyanGlow,
                    inactiveTrackColor = BorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .testTag("video_scrubber")
            )

            // Playback Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause + Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("button_play_pause")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = CyanGlow
                        )
                    }

                    Text(
                        text = "${String.format("%.1f", currentProgress * project.durationSeconds)}s / ${project.durationSeconds}.0s",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Speed Selector (0.5x, 1x, 2x)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(0.5f, 1.0f, 2.0f).forEach { speed ->
                        val isSpeedActive = playbackSpeed == speed
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSpeedActive) SurfaceCardElevated else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSpeedActive) CyanGlow else BorderSubtle),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { playbackSpeed = speed }
                        ) {
                            Text(
                                text = "${speed}x",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSpeedActive) CyanGlow else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Share & Download Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "PAP AI Video Generator")
                                putExtra(Intent.EXTRA_TEXT, "Lihat video gerakan manusia asli yang saya buat dengan PAP AI Generator:\n\nPrompt: ${project.prompt}\nEngine: ${project.engine} (${project.aspectRatio})")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Video PAP AI"))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("button_share_video")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextPrimary
                        )
                    }

                    IconButton(
                        onClick = onRegenerate,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("button_regenerate_video")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerate",
                            tint = VioletNeon
                        )
                    }
                }
            }

            // Prompt summary description
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = project.prompt,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                ),
                maxLines = 2
            )
        }
    }
}
