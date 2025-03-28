@file:Suppress("Filename")

package com.cramsan.edifikana.client.lib.features.auth.signup

/**
 * Sign up form UI model.
 */
data class SignUpFormUIModel(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val policyChecked: Boolean,
    val registerEnabled: Boolean,
    val errorMessage: List<String>?,
)
