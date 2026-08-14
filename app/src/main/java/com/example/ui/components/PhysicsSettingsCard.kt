package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CameraMovement
import com.example.data.model.PhysicsSettings
import com.example.ui.theme.*

@Composable
fun PhysicsSettingsCard(
    physicsSettings: PhysicsSettings,
    onPhysicsChanged: (PhysicsSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, if (isExpanded) CyanGlow.copy(alpha = 0.5f) else BorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = CyanGlow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Fisika Gerakan Asli (DiT Dynamics)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Gravitasi ${physicsSettings.gravityStrength}m/s² • Lipatan Kain ${(physicsSettings.clothFoldFidelity * 100).toInt()}% • Kerutan Wajah ${(physicsSettings.facialMicroExpression * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = CyanGlow
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider(color = BorderSubtle, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Gravity Strength
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Efek Gravitasi Bumi (m/s²)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        )
                        Text(
                            text = "${String.format("%.1f", physicsSettings.gravityStrength)} m/s²",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyanGlow, fontWeight = FontWeight.Bold)
                        )
                    }
                    Slider(
                        value = physicsSettings.gravityStrength,
                        onValueChange = { onPhysicsChanged(physicsSettings.copy(gravityStrength = it)) },
                        valueRange = 0f..20f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanGlow,
                            activeTrackColor = CyanGlow,
                            inactiveTrackColor = BorderSubtle
                        ),
                        modifier = Modifier.testTag("slider_gravity")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Cloth folds & natural wrinkles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lipatan Kain & Kerutan Pakaian Dinamis",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        )
                        Text(
                            text = "${(physicsSettings.clothFoldFidelity * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall.copy(color = VioletNeon, fontWeight = FontWeight.Bold)
                        )
                    }
                    Slider(
                        value = physicsSettings.clothFoldFidelity,
                        onValueChange = { onPhysicsChanged(physicsSettings.copy(clothFoldFidelity = it)) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = VioletNeon,
                            activeTrackColor = VioletNeon,
                            inactiveTrackColor = BorderSubtle
                        ),
                        modifier = Modifier.testTag("slider_cloth_folds")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Facial wrinkles & micro-expressions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kerutan & Ekspresi Mikro Wajah",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        )
                        Text(
                            text = "${(physicsSettings.facialMicroExpression * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall.copy(color = CoralAccent, fontWeight = FontWeight.Bold)
                        )
                    }
                    Slider(
                        value = physicsSettings.facialMicroExpression,
                        onValueChange = { onPhysicsChanged(physicsSettings.copy(facialMicroExpression = it)) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = CoralAccent,
                            activeTrackColor = CoralAccent,
                            inactiveTrackColor = BorderSubtle
                        ),
                        modifier = Modifier.testTag("slider_facial_wrinkles")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Camera Movement
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = AmberGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pergerakan Kamera Sinematik",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CameraMovement.values().forEach { cam ->
                            val isCamSelected = cam == physicsSettings.cameraMovement
                            FilterChip(
                                selected = isCamSelected,
                                onClick = { onPhysicsChanged(physicsSettings.copy(cameraMovement = cam)) },
                                label = {
                                    Text(
                                        text = cam.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = if (isCamSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AmberGlow.copy(alpha = 0.2f),
                                    selectedLabelColor = AmberGlow,
                                    containerColor = SurfaceCardElevated,
                                    labelColor = TextSecondary
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isCamSelected) AmberGlow else BorderSubtle
                                ),
                                modifier = Modifier.testTag("chip_cam_${cam.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }
    }
}
