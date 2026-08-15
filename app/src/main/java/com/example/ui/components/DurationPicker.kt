package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class DurationItem(
    val seconds: Int,
    val label: String,
    val frameCount: Int,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationPicker(
    selectedDuration: Int,
    onDurationSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val durationList = listOf(
        DurationItem(3, "3 Detik", 72, "Quick Preview • 72 Frames (Ultra Fast)"),
        DurationItem(5, "5 Detik", 120, "Standar DiT • 120 Frames (Direkomendasikan)"),
        DurationItem(8, "8 Detik", 192, "Cinematic Motion • 192 Frames"),
        DurationItem(10, "10 Detik", 240, "Full Sequence • 240 Frames"),
        DurationItem(15, "15 Detik", 360, "Extended Motion • 360 Frames"),
        DurationItem(20, "20 Detik", 480, "Long Scene • 480 Frames"),
        DurationItem(30, "30 Detik", 720, "Ultra HD Long • 720 Frames")
    )

    val currentItem = durationList.find { it.seconds == selectedDuration }
        ?: DurationItem(selectedDuration, "$selectedDuration Detik", selectedDuration * 24, "Custom Duration")

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = AmberGlow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Durasi Video",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
            Text(
                text = "${currentItem.seconds}s (${currentItem.frameCount}f @ 60 FPS)",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AmberGlow,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown Menu Box Container
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
                    color = if (expanded) AmberGlow else BorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .testTag("dropdown_duration_picker")
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberGlow.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${currentItem.seconds}s",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGlow
                                )
                            )
                        }

                        Column {
                            Text(
                                text = currentItem.label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = currentItem.description,
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

            // Dropdown Menu List
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(SurfaceCardElevated)
                    .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(12.dp))
            ) {
                durationList.forEach { item ->
                    val isSelected = item.seconds == selectedDuration
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${item.label} (${item.seconds} Detik)",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) AmberGlow else TextPrimary
                                        )
                                    )
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isSelected) AmberGlow.copy(alpha = 0.8f) else TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = AmberGlow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (isSelected) AmberGlow else TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            onDurationSelected(item.seconds)
                            expanded = false
                        },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier
                            .testTag("duration_option_${item.seconds}s")
                            .background(
                                if (isSelected) AmberGlow.copy(alpha = 0.12f) else SurfaceCardElevated
                            )
                    )
                }
            }
        }
    }
}
