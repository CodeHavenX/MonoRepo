@file:Suppress("Filename")

package com.cramsan.edifikana.client.lib.features.root.auth.signup

/**
 * Sign up form UI model.
 */
data class SignUpFormUIModel(
    val email: String,
    val password: String,
    val repeatPassword: String,
    val errorMessage: String?,
)
