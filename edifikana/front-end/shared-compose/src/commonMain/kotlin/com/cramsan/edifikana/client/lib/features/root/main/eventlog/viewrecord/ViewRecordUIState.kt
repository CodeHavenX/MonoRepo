package com.cramsan.edifikana.client.lib.features.root.main.eventlog.viewrecord

/**
 * Represents the UI state of the View Record screen.
 */
data class ViewRecordUIState(
    val record: ViewRecordUIModel?,
    val isLoading: Boolean,
    val title: String,
)
