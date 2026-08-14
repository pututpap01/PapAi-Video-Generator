package com.example.data.repository

import com.example.data.local.VideoProjectDao
import com.example.data.local.VideoProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val dao: VideoProjectDao) {
    val allProjects: Flow<List<VideoProjectEntity>> = dao.getAllProjects()

    suspend fun saveProject(project: VideoProjectEntity): Long {
        return dao.insertProject(project)
    }

    suspend fun getProjectById(id: Long): VideoProjectEntity? {
        return dao.getProjectById(id)
    }

    suspend fun deleteProject(project: VideoProjectEntity) {
        dao.deleteProject(project)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
