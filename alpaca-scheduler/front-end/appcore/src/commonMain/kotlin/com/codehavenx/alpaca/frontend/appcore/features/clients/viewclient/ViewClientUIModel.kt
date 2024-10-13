package com.codehavenx.alpaca.frontend.appcore.features.clients.viewclient

/**
 * UI Model for the View Client screen.
 */
data class ViewClientUIModel(
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
