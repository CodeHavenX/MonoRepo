package com.cramsan.framework.sample.shared.features.main.welcome

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

/**
 * Welcome dialog screen.
 *
 * Demonstrates the result-returning navigation API: navigates back with a
 * [ThemeSelection] result that the caller receives via [ObserveNavResult].
 */
@Composable
fun WelcomeDialogScreen(
    viewModel: WelcomeDialogViewModel = koinViewModel(),
) {
    AlertDialog(
        onDismissRequest = { viewModel.dismiss() },
        title = { Text("Welcome!") },
        text = { Text("Choose your preferred theme to get started.") },
        confirmButton = {
            TextButton(onClick = { viewModel.selectTheme(ThemeSelection.DARK) }) {
                Text("Dark")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.selectTheme(ThemeSelection.LIGHT) }) {
                Text("Light")
            }
        },
    )
}
