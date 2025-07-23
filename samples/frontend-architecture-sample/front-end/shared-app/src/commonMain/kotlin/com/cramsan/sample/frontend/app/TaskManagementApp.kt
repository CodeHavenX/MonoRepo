package com.cramsan.sample.frontend.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.cramsan.sample.frontend.app.di.appModule
import com.cramsan.sample.frontend.app.screens.TaskListScreen
import com.cramsan.sample.frontend.shared.data.InMemoryTaskRepository
import com.cramsan.sample.frontend.shared.data.SampleTaskData
import com.cramsan.sample.frontend.ui.theme.TaskManagementTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

/**
 * Main application composable that sets up the app structure.
 * Demonstrates app-level architecture and initialization.
 */
@Composable
fun TaskManagementApp() {
    KoinApplication(
        application = {
            modules(appModule)
        }
    ) {
        TaskManagementTheme {
            AppContent()
        }
    }
}

@Composable
private fun AppContent() {
    val repository: InMemoryTaskRepository = koinInject()

    // Initialize with sample data on first launch
    LaunchedEffect(Unit) {
        SampleTaskData.initializeWithSampleData(repository)
    }

    TaskListScreen()
}
