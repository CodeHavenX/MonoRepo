package com.cramsan.edifikana.client.lib.features.settings.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.components.themetoggle.SelectedTheme
import org.koin.compose.viewmodel.koinViewModel

/**
 * Settings screen with a theme toggle and back navigation.
 */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize()
    }

    ObserveViewModelEvents(viewModel) { _ ->
        // No events to handle for now
    }

    SettingsContent(
        uiState = uiState,
        onThemeSelected = { selectedTheme ->
            viewModel.changeSelectedTheme(selectedTheme)
        },
        onBackSelected = {
            viewModel.navigateBack()
        },
    )
}

/**
 * Reusable content for the settings screen to allow previews and unit testing without Koin.
 * This layout follows the stacked column approach used across the app: a header, description
 * and stacked selectable options.
 */
@Composable
internal fun SettingsContent(
    uiState: SettingsUIState,
    onThemeSelected: (SelectedTheme) -> Unit,
    onBackSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "Settings",
                onNavigationIconSelected = {
                    onBackSelected()
                },
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            sectionContent = { mod ->
                Column(
                    modifier = mod,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    // Light
                    SettingOption(
                        title = "Light",
                        subtitle = "Use light colors",
                        selected = uiState.selectedTheme == SelectedTheme.LIGHT,
                        onClick = { onThemeSelected(SelectedTheme.LIGHT) },
                    )

                    // Dark
                    SettingOption(
                        title = "Dark",
                        subtitle = "Use dark colors",
                        selected = uiState.selectedTheme == SelectedTheme.DARK,
                        onClick = { onThemeSelected(SelectedTheme.DARK) },
                    )

                    // System default
                    SettingOption(
                        title = "System default",
                        subtitle = "Follow the system appearance",
                        selected = uiState.selectedTheme == SelectedTheme.SYSTEM_DEFAULT,
                        onClick = { onThemeSelected(SelectedTheme.SYSTEM_DEFAULT) },
                    )
                }
            },
            buttonContent = { /* no actions */ },
        )
    }
}

@Composable
private fun SettingOption(title: String, subtitle: String? = null, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        RadioButton(
            selected = selected,
            onClick = null,
        )
    }
}
