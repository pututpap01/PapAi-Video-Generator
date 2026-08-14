package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.*

@Composable
fun ImageReferencePicker(
    selectedImageUri: Uri?,
    poseAnalysis: String?,
    isAnalyzing: Boolean,
    onImageSelected: (Uri?) -> Unit,
    onAnalyzeImage: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = EmeraldGlow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Gambar Referensi Gerakan",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            if (selectedImageUri != null) {
                TextButton(
                    onClick = { onImageSelected(null) },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Hapus Gambar",
                        style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedImageUri == null) {
            // Empty / Upload state
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("button_pick_image")
                    .clickable { photoPickerLauncher.launch("image/*") }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceCardElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Upload Photo",
                            tint = CyanGlow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Unggah Foto Referensi Gerakan (Pose / Pakaian)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Dianalisis oleh Gemini 3.1 Pro untuk kinematic pose & lipatan kain",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset Quick Sample Avatars
                    Text(
                        text = "Atau pilih pose sampel gerakan:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val samplePresets = listOf(
                            "Penari Gaun Sutra" to "android.resource://com.aistudio.papvideo.wvxrq/drawable/hero_motion_banner",
                            "Pelari Dinamis" to "preset://athletic_runner",
                            "Model Street Runway" to "preset://street_model",
                            "Silat Bela Diri" to "preset://martial_arts"
                        )

                        samplePresets.forEach { (title, uriStr) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceCardElevated,
                                border = BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier
                                    .clickable {
                                        onImageSelected(Uri.parse(uriStr))
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsRun,
                                        contentDescription = null,
                                        tint = EmeraldGlow,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextPrimary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Selected Image Preview + Gemini Vision Analysis card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCard,
                border = BorderStroke(1.5.dp, EmeraldGlow.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceCardElevated)
                        ) {
                            if (selectedImageUri.toString().contains("hero_motion_banner")) {
                                Image(
                                    painter = painterResource(id = R.drawable.hero_motion_banner),
                                    contentDescription = "Sample Reference",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Reference Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldGlow.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "GEMINI 3.1 PRO VISION",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = EmeraldGlow,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                                if (isAnalyzing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        strokeWidth = 2.dp,
                                        color = EmeraldGlow
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (isAnalyzing) {
                                    "Menganalisis anatomi, pose, dan elastisitas kain..."
                                } else {
                                    poseAnalysis ?: "Pose referensi siap diterapkan ke pipeline video DiT."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (poseAnalysis != null) TextPrimary else TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                ),
                                maxLines = 3
                            )
                        }

                        IconButton(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change Image",
                                tint = CyanGlow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
