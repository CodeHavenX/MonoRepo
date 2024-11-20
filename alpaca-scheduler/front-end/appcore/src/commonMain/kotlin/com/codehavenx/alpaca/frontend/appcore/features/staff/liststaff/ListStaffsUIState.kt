package com.codehavenx.alpaca.frontend.appcore.features.staff.liststaff

/**
 * UI State for the List Staff screen.
 */
data class ListStaffsUIState(
    val users: StaffPageUIModel,
    val pagination: StaffPaginationUIModel,
    val isLoading: Boolean,
)
