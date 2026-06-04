package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events emitted by [FeatureReplacemeViewModel] and consumed by [FeatureReplacemeScreen].
 *
 * Add sealed subclasses here for one-shot actions that shouldn't live in [FeatureReplacemeUIState]
 * (e.g., navigation triggers, snackbar messages).
 */
sealed class FeatureReplacemeEvent : ViewModelEvent {
    /** Placeholder event — replace with your screen's real events. */
    data object Noop : FeatureReplacemeEvent()
}
