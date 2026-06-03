package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Immutable UI state for [FeatureReplacemeScreen].
 *
 * Add fields here to represent all the data the screen needs to render. The ViewModel
 * produces a fresh copy for every state change via `updateUiState { it.copy(...) }`.
 *
 * Design guidelines:
 * - Keep this a plain data class — no logic, no side effects.
 * - Prefer nullable fields over a separate "loading" hierarchy for simple screens.
 * - Use sealed subclasses inside this file only for discriminated list items or
 *   polymorphic content (e.g. `sealed interface Item`).
 *
 * Example with more fields:
 * ```
 * data class FeatureReplacemeUIState(
 *     val isLoading: Boolean,
 *     val items: List<String> = emptyList(),
 *     val errorMessage: String? = null,
 * ) : ViewModelUIState {
 *     companion object {
 *         val Initial = FeatureReplacemeUIState(isLoading = false)
 *     }
 * }
 * ```
 *
 * TODO: Add fields for the data this screen needs to render.
 */
data class FeatureReplacemeUIState(
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        /** The state the screen starts in before any data is loaded. */
        val Initial = FeatureReplacemeUIState(isLoading = false)
    }
}
