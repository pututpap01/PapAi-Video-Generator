package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatio as AspectRatioEnum
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AspectPicker(
    selectedRatio: AspectRatioEnum,
    onRatioSelected: (AspectRatioEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AspectRatio,
                    contentDescription = null,
                    tint = CyanGlow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Aspek Rasio",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
            Text(
                text = "${selectedRatio.ratioLabel} (${selectedRatio.title})",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyanGlow,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown Menu Box
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceCard,
                border = BorderStroke(
                    width = if (expanded) 1.5.dp else 1.dp,
                    color = if (expanded) CyanGlow else BorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .testTag("dropdown_aspect_ratio_picker")
                    .clickable { expanded = !expanded }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Ratio visual shape representation
                        Box(
                            modifier = Modifier
                                .height(26.dp)
                                .width(
                                    when (selectedRatio) {
                                        AspectRatioEnum.PORTRAIT_9_16 -> 15.dp
                                        AspectRatioEnum.LANDSCAPE_16_9 -> 26.dp
                                        AspectRatioEnum.SQUARE_1_1 -> 22.dp
                                        AspectRatioEnum.CLASSIC_4_3 -> 24.dp
                                        AspectRatioEnum.ULTRAWIDE_21_9 -> 28.dp
                                    }
                                )
                                .clip(RoundedCornerShape(3.dp))
                                .background(CyanGlow)
                        )

                        Column {
                            Text(
                                text = "${selectedRatio.ratioLabel} — ${selectedRatio.title}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = when (selectedRatio) {
                                    AspectRatioEnum.PORTRAIT_9_16 -> "Cocok untuk TikTok, IG Reels, YouTube Shorts"
                                    AspectRatioEnum.LANDSCAPE_16_9 -> "Format standar YouTube, TV & Layar Lebar"
                                    AspectRatioEnum.SQUARE_1_1 -> "Cocok untuk Feed Instagram & Profil"
                                    AspectRatioEnum.CLASSIC_4_3 -> "Format Klasik Retro & Monitor Standar"
                                    AspectRatioEnum.ULTRAWIDE_21_9 -> "Format Sinematik Teatrikal Anamorphic"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            }

            // Dropdown List Items
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(SurfaceCardElevated)
                    .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(12.dp))
            ) {
                AspectRatioEnum.values().forEach { ratio ->
                    val isSelected = ratio == selectedRatio
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(22.dp)
                                            .width(
                                                when (ratio) {
                                                    AspectRatioEnum.PORTRAIT_9_16 -> 13.dp
                                                    AspectRatioEnum.LANDSCAPE_16_9 -> 24.dp
                                                    AspectRatioEnum.SQUARE_1_1 -> 18.dp
                                                    AspectRatioEnum.CLASSIC_4_3 -> 20.dp
                                                    AspectRatioEnum.ULTRAWIDE_21_9 -> 26.dp
                                                }
                                            )
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (isSelected) CyanGlow else TextSecondary.copy(alpha = 0.5f))
                                    )

                                    Column {
                                        Text(
                                            text = "${ratio.ratioLabel} • ${ratio.title}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) CyanGlow else TextPrimary
                                            )
                                        )
                                        Text(
                                            text = when (ratio) {
                                                AspectRatioEnum.PORTRAIT_9_16 -> "TikTok, Reels, Shorts (Vertical)"
                                                AspectRatioEnum.LANDSCAPE_16_9 -> "YouTube, Cinema, Landscape HD"
                                                AspectRatioEnum.SQUARE_1_1 -> "Feed Instagram 1:1"
                                                AspectRatioEnum.CLASSIC_4_3 -> "Classic TV Standard"
                                                AspectRatioEnum.ULTRAWIDE_21_9 -> "21:9 Ultrawide Cinematic"
                                            },
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isSelected) CyanGlow.copy(alpha = 0.8f) else TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = CyanGlow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        onClick = {
                            onRatioSelected(ratio)
                            expanded = false
                        },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier
                            .testTag("aspect_option_${ratio.ratioLabel.replace(":", "_")}")
                            .background(
                                if (isSelected) CyanGlow.copy(alpha = 0.12f) else SurfaceCardElevated
                            )
                    )
                }
            }
        }
    }
}
