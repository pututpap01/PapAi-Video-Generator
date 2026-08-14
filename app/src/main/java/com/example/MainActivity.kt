package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ModalSettingsScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.VideoGeneratorViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VideoGeneratorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    val selectedTab by viewModel.selectedTab.collectAsState()
                    val savedProjects by viewModel.savedProjects.collectAsState()

                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screen_transition"
                    ) { tab ->
                        when (tab) {
                            0 -> HomeScreen(
                                viewModel = viewModel,
                                onOpenHistory = { viewModel.selectedTab.value = 1 },
                                onOpenModalSettings = { viewModel.selectedTab.value = 2 }
                            )
                            1 -> HistoryScreen(
                                projects = savedProjects,
                                onSelectProject = { project ->
                                    viewModel.selectProjectForPlayback(project)
                                },
                                onLoadIntoEditor = { project ->
                                    viewModel.loadProjectIntoEditor(project)
                                },
                                onDeleteProject = { project ->
                                    viewModel.deleteProject(project)
                                },
                                onBack = { viewModel.selectedTab.value = 0 }
                            )
                            2 -> ModalSettingsScreen(
                                modalConfigManager = viewModel.modalConfigManager,
                                onBack = { viewModel.selectedTab.value = 0 }
                            )
                        }
                    }
                }
            }
        }
    }
}
