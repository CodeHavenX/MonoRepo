package com.codehavenx.alpaca.frontend.appcore.models

/**
 * A staff member domain model.
 */
data class Client(
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
