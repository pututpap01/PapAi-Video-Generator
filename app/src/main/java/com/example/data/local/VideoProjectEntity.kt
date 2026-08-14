package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class VideoProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prompt: String,
    val enhancedPrompt: String,
    val aspectRatio: String,
    val durationSeconds: Int,
    val engine: String,
    val style: String,
    val cameraMovement: String,
    val referenceImageUri: String?,
    val referencePoseAnalysis: String?,
    val videoUrl: String?,
    val localVideoPath: String?,
    val previewThumbnailUrl: String?,
    val gravity: Float,
    val clothFoldFidelity: Float,
    val facialFidelity: Float,
    val timestamp: Long = System.currentTimeMillis()
)
