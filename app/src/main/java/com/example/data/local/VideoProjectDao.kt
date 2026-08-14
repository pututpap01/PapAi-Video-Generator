package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoProjectDao {
    @Query("SELECT * FROM video_projects ORDER BY timestamp DESC")
    fun getAllProjects(): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): VideoProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: VideoProjectEntity): Long

    @Update
    suspend fun updateProject(project: VideoProjectEntity)

    @Delete
    suspend fun deleteProject(project: VideoProjectEntity)

    @Query("DELETE FROM video_projects")
    suspend fun clearAll()
}
