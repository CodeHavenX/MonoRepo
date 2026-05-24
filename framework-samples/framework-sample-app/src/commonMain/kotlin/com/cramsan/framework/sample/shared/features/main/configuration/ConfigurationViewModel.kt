package com.cramsan.framework.sample.shared.features.main.configuration

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.configuration.Configuration
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import kotlinx.coroutines.launch

private const val SAMPLE_KEY = "sample_config_key"

/**
 * ViewModel for the Configuration screen.
 */
@FrontendViewModel
class ConfigurationViewModel(dependencies: ViewModelDependencies, private val configuration: Configuration) :
    BaseViewModel<ConfigurationEvent, ConfigurationUIState>(
        dependencies,
        ConfigurationUIState.Initial,
        TAG,
    ) {
    /**
     * Read a string value from configuration and update the UI state.
     */
    fun readString() {
        viewModelCoroutineScope.launch {
            val value = configuration.readString(SAMPLE_KEY)
            updateUiState { current ->
                when (current) {
                    is ConfigurationUIState.NotRead -> {
                        ConfigurationUIState.Read(
                            stringValue = value,
                            intValue = null,
                            longValue = null,
                            booleanValue = null,
                        )
                    }

                    is ConfigurationUIState.Read -> {
                        current.copy(stringValue = value)
                    }
                }
            }
        }
    }

    /**
     * Read an int value from configuration and update the UI state.
     */
    fun readInt() {
        viewModelCoroutineScope.launch {
            val value = configuration.readInt(SAMPLE_KEY)
            updateUiState { current ->
                when (current) {
                    is ConfigurationUIState.NotRead -> {
                        ConfigurationUIState.Read(
                            stringValue = null,
                            intValue = value,
                            longValue = null,
                            booleanValue = null,
                        )
                    }

                    is ConfigurationUIState.Read -> {
                        current.copy(intValue = value)
                    }
                }
            }
        }
    }

    /**
     * Read a long value from configuration and update the UI state.
     */
    fun readLong() {
        viewModelCoroutineScope.launch {
            val value = configuration.readLong(SAMPLE_KEY)
            updateUiState { current ->
                when (current) {
                    is ConfigurationUIState.NotRead -> {
                        ConfigurationUIState.Read(
                            stringValue = null,
                            intValue = null,
                            longValue = value,
                            booleanValue = null,
                        )
                    }

                    is ConfigurationUIState.Read -> {
                        current.copy(longValue = value)
                    }
                }
            }
        }
    }

    /**
     * Read a boolean value from configuration and update the UI state.
     */
    fun readBoolean() {
        viewModelCoroutineScope.launch {
            val value = configuration.readBoolean(SAMPLE_KEY)
            updateUiState { current ->
                when (current) {
                    is ConfigurationUIState.NotRead -> {
                        ConfigurationUIState.Read(
                            stringValue = null,
                            intValue = null,
                            longValue = null,
                            booleanValue = value,
                        )
                    }

                    is ConfigurationUIState.Read -> {
                        current.copy(booleanValue = value)
                    }
                }
            }
        }
    }

    /**
     * Navigate back to the main menu.
     */
    fun navigateBack() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "ConfigurationViewModel"
    }
}
