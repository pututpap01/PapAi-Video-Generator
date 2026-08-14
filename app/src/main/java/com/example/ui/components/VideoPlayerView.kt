package com.example.ui.components

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.VideoProjectEntity
import com.example.data.model.AspectRatio
import com.example.ui.theme.*

@Composable
fun VideoPlayerView(
    project: VideoProjectEntity,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableStateOf(0) }
    var durationMs by remember { mutableStateOf(project.durationSeconds * 1000) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var showControls by remember { mutableStateOf(true) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    // Human realistic backdrop images from CDN matching the style
    val realisticHumanBackdrops = listOf(
        "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=1080&q=80"
    )
    val backdropUrl = project.previewThumbnailUrl
        ?: realisticHumanBackdrops[Math.abs(project.prompt.hashCode()) % realisticHumanBackdrops.size]

    val aspectEnum = AspectRatio.values().find { it.ratioLabel == project.aspectRatio } ?: AspectRatio.PORTRAIT_9_16
    val aspectRatioFloat = aspectEnum.widthRatio / aspectEnum.heightRatio

    // Video URL to play
    val videoUri = project.videoUrl?.let { Uri.parse(it) }

    // Progress update timer
    LaunchedEffect(videoViewRef, isPlaying) {
        while (true) {
            videoViewRef?.let { vv ->
                if (vv.isPlaying) {
                    currentPositionMs = vv.currentPosition
                    if (vv.duration > 0) durationMs = vv.duration
                }
            }
            kotlinx.coroutines.delay(200)
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
                            text = project.engine.take(20),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = VioletNeon,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(EmeraldGlow.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "REAL 1080P",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Lihat video manusia realistik yang dihasilkan dengan ${project.engine}:\n${project.videoUrl ?: project.prompt}")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Bagikan Video"))
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share Video", tint = CyanGlow, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Real Video Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 240.dp, max = 380.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundDark)
                    .clickable { showControls = !showControls },
                contentAlignment = Alignment.Center
            ) {
                // Background Poster / Fallback Real Human Photo
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(backdropUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Realistic Human Visual",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark cinematic overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // Native Hardware Video Player (Streams authentic MP4 human motion)
                if (videoUri != null) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                setVideoURI(videoUri)
                                setOnPreparedListener { mp ->
                                    mp.isLooping = true
                                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                                    isBuffering = false
                                    if (isPlaying) {
                                        start()
                                    }
                                }
                                setOnErrorListener { _, _, _ ->
                                    isBuffering = false
                                    true // Handled gracefully
                                }
                                videoViewRef = this
                            }
                        },
                        update = { view ->
                            if (isPlaying && !view.isPlaying) {
                                view.start()
                            } else if (!isPlaying && view.isPlaying) {
                                view.pause()
                            }
                        },
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(aspectRatioFloat)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                // Buffering indicator
                if (isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = CyanGlow,
                        strokeWidth = 3.dp
                    )
                }

                // Play / Pause Center Overlay Button
                if (showControls || !isPlaying) {
                    IconButton(
                        onClick = {
                            isPlaying = !isPlaying
                            videoViewRef?.let {
                                if (isPlaying) it.start() else it.pause()
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .testTag("button_play_pause_video")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = CyanGlow,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Bottom HUD: Video timeline, duration, physics tags
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column {
                        // Time & Physics HUD
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val currentSec = currentPositionMs / 1000
                            val totalSec = (durationMs / 1000).coerceAtLeast(project.durationSeconds)
                            Text(
                                text = String.format("00:%02d / 00:%02d", currentSec, totalSec),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Gravity: ${project.gravity}m/s²",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyanGlow,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "Cloth: ${(project.clothFoldFidelity * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = VioletNeon,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Progress bar
                        val progressFraction = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = CyanGlow,
                            trackColor = BorderSubtle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prompt summary
            Text(
                text = project.prompt,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons (Speed, Regenerate, Replay)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playback speed chips
                listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { speed ->
                    val isSpeedSelected = playbackSpeed == speed
                    FilterChip(
                        selected = isSpeedSelected,
                        onClick = { playbackSpeed = speed },
                        label = {
                            Text(
                                text = "${speed}x",
                                fontSize = 10.sp,
                                fontWeight = if (isSpeedSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VioletNeon.copy(alpha = 0.25f),
                            selectedLabelColor = VioletNeon,
                            containerColor = SurfaceCardElevated,
                            labelColor = TextSecondary
                        ),
                        border = BorderStroke(1.dp, if (isSpeedSelected) VioletNeon else BorderSubtle),
                        modifier = Modifier.height(28.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onRegenerate,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("button_regenerate_video")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Regenerate", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
