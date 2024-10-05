package com.cramsan.edifikana.client.lib.features.signinv2

/**
 * Sign in v2 UI model.
 */
object SignInV2UIModel {

    /**
     * Sign in form UI model.
     */
    data class SignInFormUIModel(
        val username: String,
        val password: String,
    )
}
