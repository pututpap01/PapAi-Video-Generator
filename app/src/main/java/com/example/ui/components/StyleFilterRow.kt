package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MotionStyle
import com.example.ui.theme.*

@Composable
fun StyleFilterRow(
    selectedStyle: MotionStyle,
    onStyleSelected: (MotionStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = null,
                    tint = CoralAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Gaya & Filter Visual",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
            Text(
                text = selectedStyle.tag,
                style = MaterialTheme.typography.labelSmall.copy(color = CoralAccent, fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MotionStyle.values().forEach { style ->
                val isSelected = style == selectedStyle
                val iconVector: ImageVector = when (style) {
                    MotionStyle.HYPER_REALISTIC -> Icons.Default.Bolt
                    MotionStyle.CINEMATIC_MASTER -> Icons.Default.Movie
                    MotionStyle.STUDIO_FASHION -> Icons.Default.Checkroom
                    MotionStyle.DYNAMIC_ATHLETIC -> Icons.Default.DirectionsRun
                    MotionStyle.STREET_AUTHENTIC -> Icons.Default.DirectionsWalk
                    MotionStyle.SLOW_MO_ELEGANCE -> Icons.Default.SlowMotionVideo
                    MotionStyle.CYBER_NEON -> Icons.Default.AutoAwesome
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) SurfaceCardElevated else SurfaceCard,
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) CoralAccent else BorderSubtle
                    ),
                    modifier = Modifier
                        .testTag("style_chip_${style.name.lowercase()}")
                        .clickable { onStyleSelected(style) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = if (isSelected) CoralAccent else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = style.title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
