package com.example.ui.components

import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.view.Surface
import android.view.TextureView
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
import androidx.compose.ui.platform.LocalUriHandler
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
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerView(
    project: VideoProjectEntity,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableStateOf(0) }
    var durationMs by remember { mutableStateOf(project.durationSeconds * 1000) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var showControls by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(true) }
    var hasPlaybackError by remember { mutableStateOf(false) }

    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }
    var surfaceRef by remember { mutableStateOf<Surface?>(null) }

    val aspectEnum = AspectRatio.values().find { it.ratioLabel == project.aspectRatio } ?: AspectRatio.PORTRAIT_9_16
    val aspectRatioFloat = aspectEnum.widthRatio / aspectEnum.heightRatio

    val realisticHumanBackdrops = listOf(
        "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=1080&q=80"
    )
    val backdropUrl = project.previewThumbnailUrl
        ?: realisticHumanBackdrops[Math.abs(project.prompt.hashCode()) % realisticHumanBackdrops.size]

    val validVideoUrl = project.videoUrl?.takeIf { it.startsWith("http://") || it.startsWith("https://") }
        ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"

    // Timer loop for progress updates
    LaunchedEffect(mediaPlayerRef, isPlaying) {
        while (true) {
            mediaPlayerRef?.let { mp ->
                try {
                    if (mp.isPlaying) {
                        currentPositionMs = mp.currentPosition
                        if (mp.duration > 0) durationMs = mp.duration
                        isBuffering = false
                    }
                } catch (_: Exception) {}
            }
            delay(150)
        }
    }

    // Clean up MediaPlayer on disposal
    DisposableEffect(validVideoUrl) {
        onDispose {
            try {
                mediaPlayerRef?.stop()
                mediaPlayerRef?.release()
                mediaPlayerRef = null
                surfaceRef?.release()
                surfaceRef = null
            } catch (_: Exception) {}
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = BorderStroke(1.5.dp, CyanGlow.copy(alpha = 0.7f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Bar Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
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
                            text = "LIVE MOTION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            try {
                                uriHandler.openUri(validVideoUrl)
                            } catch (_: Exception) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validVideoUrl))
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Buka Link Video",
                            tint = CyanGlow,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Video gerakan manusia realistis hasil ${project.engine}:\n$validVideoUrl"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Video"))
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Video",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Video Viewport (TextureView-backed Hardware Video Player)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 260.dp, max = 380.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundDark)
                    .clickable { showControls = !showControls },
                contentAlignment = Alignment.Center
            ) {
                // Background Poster / Fallback Image (shown while buffering or if video error)
                if (isBuffering || hasPlaybackError) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(backdropUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Realistic Human Visual",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Hardware-Accelerated TextureView Video Player
                AndroidView(
                    factory = { ctx ->
                        TextureView(ctx).apply {
                            surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                                override fun onSurfaceTextureAvailable(st: SurfaceTexture, width: Int, height: Int) {
                                    val surface = Surface(st)
                                    surfaceRef = surface
                                    try {
                                        val mp = MediaPlayer().apply {
                                            setDataSource(ctx, Uri.parse(validVideoUrl))
                                            setSurface(surface)
                                            isLooping = true
                                            setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
                                            setOnPreparedListener { player ->
                                                isBuffering = false
                                                hasPlaybackError = false
                                                if (isPlaying) {
                                                    player.start()
                                                }
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    try {
                                                        player.playbackParams = PlaybackParams().apply {
                                                            speed = playbackSpeed
                                                        }
                                                    } catch (_: Exception) {}
                                                }
                                            }
                                            setOnBufferingUpdateListener { _, _ ->
                                                isBuffering = false
                                            }
                                            setOnErrorListener { _, _, _ ->
                                                isBuffering = false
                                                hasPlaybackError = true
                                                true
                                            }
                                            prepareAsync()
                                        }
                                        mediaPlayerRef = mp
                                    } catch (e: Exception) {
                                        hasPlaybackError = true
                                        isBuffering = false
                                    }
                                }

                                override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, width: Int, height: Int) {}
                                override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                                    mediaPlayerRef?.stop()
                                    mediaPlayerRef?.release()
                                    mediaPlayerRef = null
                                    surfaceRef?.release()
                                    surfaceRef = null
                                    return true
                                }
                                override fun onSurfaceTextureUpdated(st: SurfaceTexture) {
                                    isBuffering = false
                                }
                            }
                        }
                    },
                    update = {
                        mediaPlayerRef?.let { mp ->
                            try {
                                if (isPlaying && !mp.isPlaying) {
                                    mp.start()
                                } else if (!isPlaying && mp.isPlaying) {
                                    mp.pause()
                                }
                                mp.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    mp.playbackParams = PlaybackParams().apply { speed = playbackSpeed }
                                }
                            } catch (_: Exception) {}
                        }
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(aspectRatioFloat)
                        .clip(RoundedCornerShape(8.dp))
                )

                // Buffering Spinner
                if (isBuffering && !hasPlaybackError) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp),
                            color = CyanGlow,
                            strokeWidth = 3.dp
                        )
                    }
                }

                // Play / Pause Center Overlay Button
                if (showControls || !isPlaying) {
                    IconButton(
                        onClick = {
                            isPlaying = !isPlaying
                            mediaPlayerRef?.let {
                                try {
                                    if (isPlaying) it.start() else it.pause()
                                } catch (_: Exception) {}
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
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                // Mute / Unmute Button in Top Right of video
                IconButton(
                    onClick = {
                        isMuted = !isMuted
                        mediaPlayerRef?.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Mute / Unmute",
                        tint = TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Bottom HUD: Video timeline, duration, physics tags
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.92f))
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column {
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

                        // Progress bar (Interactive Scrubbing Indicator)
                        val progressFraction = if (durationMs > 0) {
                            (currentPositionMs.toFloat() / durationMs).coerceIn(0f, 1f)
                        } else 0f

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

            // Speed chips & Regenerate button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { speed ->
                    val isSpeedSelected = playbackSpeed == speed
                    FilterChip(
                        selected = isSpeedSelected,
                        onClick = {
                            playbackSpeed = speed
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                try {
                                    mediaPlayerRef?.playbackParams = PlaybackParams().apply { this.speed = speed }
                                } catch (_: Exception) {}
                            }
                        },
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
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Regenerate",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
