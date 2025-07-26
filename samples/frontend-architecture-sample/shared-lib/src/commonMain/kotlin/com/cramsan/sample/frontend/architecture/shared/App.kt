package com.cramsan.sample.frontend.architecture.shared

import androidx.compose.runtime.Composable
import com.cramsan.sample.frontend.architecture.shared.di.appModule
import com.cramsan.sample.frontend.architecture.shared.presentation.ui.screens.NotesScreen
import com.cramsan.sample.frontend.architecture.shared.presentation.ui.theme.NotesAppTheme
import org.koin.compose.KoinApplication

/**
 * Main entry point for the shared UI
 */
@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        NotesAppTheme {
            NotesScreen()
        }
    }
}