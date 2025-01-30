package com.cramsan.edifikana.client.lib.features.root.main.timecard.viewstaff

/**
 * Represents the UI state of the View Staff screen.
 */
data class ViewStaffUIState(
    val isLoading: Boolean,
    val staff: ViewStaffUIModel.StaffUIModel?,
    val records: List<ViewStaffUIModel.TimeCardRecordUIModel>,
    val title: String,
)
