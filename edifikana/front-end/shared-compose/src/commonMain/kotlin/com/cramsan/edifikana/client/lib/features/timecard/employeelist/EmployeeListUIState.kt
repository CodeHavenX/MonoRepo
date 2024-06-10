package com.cramsan.edifikana.client.lib.features.timecard.employeelist

data class EmployeeListUIState(
    val employees: List<EmployeeUIModel>,
    val isLoading: Boolean,
    val title: String,
)
