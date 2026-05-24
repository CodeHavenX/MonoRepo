package com.cramsan.framework.sample.shared.features.main.configuration

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Configuration feature.
 */
sealed class ConfigurationUIState : ViewModelUIState {
    /**
     * No configuration values have been read yet.
     */
    data object NotRead : ConfigurationUIState()

    /**
     * At least one read has been performed; each field holds the value the API returned (null = key absent).
     */
    data class Read(val stringValue: String?, val intValue: Int?, val longValue: Long?, val booleanValue: Boolean?) :
        ConfigurationUIState()

    companion object {
        val Initial: ConfigurationUIState = NotRead
    }
}
