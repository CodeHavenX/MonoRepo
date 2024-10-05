package com.cramsan.edifikana.client.lib.features.timecard.stafflist

/**
 * Represents the UI state of the Staff List screen.
 */
data class StaffListUIState(
    val staffs: List<StaffUIModel>,
    val isLoading: Boolean,
    val title: String,
)
