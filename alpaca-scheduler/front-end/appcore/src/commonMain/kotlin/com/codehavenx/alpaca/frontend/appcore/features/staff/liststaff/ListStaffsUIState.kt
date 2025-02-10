package com.codehavenx.alpaca.frontend.appcore.features.staff.liststaff

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI State for the List Staff screen.
 */
data class ListStaffsUIState(
    val users: StaffPageUIModel,
    val pagination: StaffPaginationUIModel,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = ListStaffsUIState(
            users = StaffPageUIModel(emptyList(),),
            pagination = StaffPaginationUIModel(
                firstPage = null,
                previousPage = null,
                nextPage = null,
                lastPage = null,
                pages = emptyList(),
            ),
            isLoading = false,
        )
    }
}
