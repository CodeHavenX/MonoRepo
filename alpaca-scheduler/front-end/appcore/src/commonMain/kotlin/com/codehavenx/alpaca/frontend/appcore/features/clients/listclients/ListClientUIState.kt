package com.codehavenx.alpaca.frontend.appcore.features.clients.listclients

/**
 * UI State for the List Clients screen.
 */
data class ListClientUIState(
    val users: ClientPageUIModel,
    val pagination: ClientPaginationUIModel,
    val isLoading: Boolean,
)
