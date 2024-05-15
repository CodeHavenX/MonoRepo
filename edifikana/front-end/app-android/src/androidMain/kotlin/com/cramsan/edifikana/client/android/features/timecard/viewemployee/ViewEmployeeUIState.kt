package com.cramsan.edifikana.client.android.features.timecard.viewemployee

data class ViewEmployeeUIState(
    val isLoading: Boolean,
    val employee: ViewEmployeeUIModel.EmployeeUIModel?,
    val records: List<ViewEmployeeUIModel.TimeCardRecordUIModel>,
)
