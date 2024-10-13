package com.codehavenx.alpaca.frontend.appcore.features.clients.updateclient

/**
 * UI Model for the Update Client screen.
 */
data class UpdateClientUIModel(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val city: String,
    val state: String,
    val zip: String,
    val country: String,
)
