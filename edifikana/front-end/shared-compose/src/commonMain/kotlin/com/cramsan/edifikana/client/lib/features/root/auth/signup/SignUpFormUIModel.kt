@file:Suppress("Filename")

package com.cramsan.edifikana.client.lib.features.root.auth.signup

/**
 * Sign up form UI model.
 */
data class SignUpFormUIModel(
    val fullName: String,
    val username: String,
    val password: String,
    val policyChecked: Boolean,
    val registerEnabled: Boolean,
    val errorMessage: String?,
)
