package com.codehavenx.alpaca.frontend.appcore.features.signin

/**
 * UI Model for the Sign In screen.
 */
data class SignInUIModel(
    val username: String,
    val password: String,
    val error: Boolean,
)
