package com.codehavenx.alpaca.frontend.appcore.features.clients.listclients

/**
 * UI Model for the Client Page screen.
 */
data class ClientPageUIModel(
    val users: List<ClientUIModel>,
)

/**
 * UI Model for a single client.
 */
data class ClientUIModel(
    val id: String,
    val displayName: String,
)

/**
 * UI Model for the pagination controls.
 */
data class ClientPaginationUIModel(
    val firstPage: String?,
    val nextPage: String?,
    val previousPage: String?,
    val lastPage: String?,
    val pages: List<ClientPageReferenceUIModel>,
)

/**
 * UI Model for a single page reference.
 */
data class ClientPageReferenceUIModel(
    val displayName: String,
    val id: String,
    val selected: Boolean,
)
