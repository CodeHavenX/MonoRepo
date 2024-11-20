package com.codehavenx.alpaca.frontend.appcore.features.staff.viewstaff

/**
 * UI Model for the View Staff screen.
 */
data class ViewStaffUIModel(
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
