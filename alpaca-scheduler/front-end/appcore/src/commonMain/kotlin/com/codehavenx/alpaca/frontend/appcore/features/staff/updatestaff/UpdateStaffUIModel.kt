package com.codehavenx.alpaca.frontend.appcore.features.staff.updatestaff

/**
 * UI Model for the Update Staff screen.
 */
data class UpdateStaffUIModel(
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
