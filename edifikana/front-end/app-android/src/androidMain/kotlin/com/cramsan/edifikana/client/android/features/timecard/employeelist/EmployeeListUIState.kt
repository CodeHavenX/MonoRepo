package com.cramsan.edifikana.client.android.features.timecard.employeelist

data class EmployeeListUIState(
    val employees: List<EmployeeUIModel>,
    val isLoading: Boolean,
    val title: String,
)
