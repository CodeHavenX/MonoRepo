@file:Suppress("Filename")

package com.cramsan.edifikana.client.lib.features.root.auth.signinv2

/**
 * Sign in form UI model.
 */
data class SignInFormUIModel(
    val email: String,
    val password: String,
    val errorMessage: String?,
)
