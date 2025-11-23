package com.cramsan.edifikana.client.lib.features.settings

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.models.Theme
import com.cramsan.edifikana.client.lib.settings.EdifikanaSettingKey
import com.cramsan.edifikana.client.lib.toSelectedTheme
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.ViewModelEvent
import com.cramsan.framework.core.compose.ViewModelUIState
import com.cramsan.ui.components.themetoggle.SelectedTheme
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings screen.
 * Responsible for reading and updating the selected theme preference.
 */
class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<SettingsEvent, SettingsUIState>(
    dependencies,
    SettingsUIState.Initial,
    TAG,
) {

    /**
     * Initialize and start listening to preference changes.
     */
    fun initialize() {
        viewModelScope.launch {
            // Listen for preference changes and react when the selected theme changes.
            preferencesManager.modifiedKey.collect { changedKey ->
                if (changedKey == EdifikanaSettingKey.SelectedTheme) {
                    loadSelectedTheme()
                }
            }
        }
        // Load initial value
        viewModelScope.launch { loadSelectedTheme() }
    }

    private suspend fun loadSelectedTheme() {
        val themeString = preferencesManager.getStringPreference(
            EdifikanaSettingKey.SelectedTheme,
        ).getOrNull()
        val themeMode = Theme.fromString(themeString)
        updateUiState { it.copy(selectedTheme = themeMode.toSelectedTheme()) }
    }

    /**
     * Change the selected theme preference.
     */
    fun changeSelectedTheme(theme: SelectedTheme) {
        viewModelScope.launch {
            val themeToSave = when (theme) {
                SelectedTheme.LIGHT -> Theme.LIGHT
                SelectedTheme.DARK -> Theme.DARK
                SelectedTheme.SYSTEM_DEFAULT -> Theme.SYSTEM_DEFAULT
            }
            // Persist the preference
            preferencesManager.updatePreference(
                EdifikanaSettingKey.SelectedTheme,
                themeToSave.name,
            ).getOrThrow()
            // Update UI state immediately
            updateUiState { it.copy(selectedTheme = theme) }
        }
    }

    /**
     * Navigate back to the previous screen by emitting a NavigateBack window event.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}

/**
 * Events for the Settings screen. Kept minimal for now.
 */
sealed interface SettingsEvent : ViewModelEvent {
    /**
     * No-op event used as a placeholder when no action is required.
     */
    object Noop : SettingsEvent
}

/**
 * UI State for the Settings screen.
 */
data class SettingsUIState(
    val selectedTheme: SelectedTheme,
) : ViewModelUIState {
    companion object {
        val Initial = SettingsUIState(
            selectedTheme = SelectedTheme.SYSTEM_DEFAULT,
        )
    }
}
