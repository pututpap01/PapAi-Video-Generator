package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatio
import com.example.ui.theme.*

@Composable
fun AspectPicker(
    selectedRatio: AspectRatio,
    onRatioSelected: (AspectRatio) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aspect Ratio",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = selectedRatio.title,
                style = MaterialTheme.typography.bodySmall.copy(color = CyanGlow)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AspectRatio.values().forEach { ratio ->
                val isSelected = ratio == selectedRatio
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) SurfaceCardElevated else SurfaceCard,
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) CyanGlow else BorderSubtle
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("aspect_ratio_${ratio.ratioLabel.replace(":", "_")}")
                        .clickable { onRatioSelected(ratio) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Visual box representation of ratio
                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .width(
                                    when (ratio) {
                                        AspectRatio.PORTRAIT_9_16 -> 16.dp
                                        AspectRatio.LANDSCAPE_16_9 -> 28.dp
                                        AspectRatio.SQUARE_1_1 -> 24.dp
                                        AspectRatio.CLASSIC_4_3 -> 26.dp
                                        AspectRatio.ULTRAWIDE_21_9 -> 30.dp
                                    }
                                )
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isSelected) CyanGlow else TextSecondary.copy(alpha = 0.4f))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = ratio.ratioLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
