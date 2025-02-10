package com.codehavenx.alpaca.frontend.appcore.features.clients.listclients

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI State for the List Clients screen.
 */
data class ListClientUIState(
    val users: ClientPageUIModel,
    val pagination: ClientPaginationUIModel,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = ListClientUIState(
            users = ClientPageUIModel(emptyList(),),
            pagination = ClientPaginationUIModel(
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
