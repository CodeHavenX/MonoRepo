package com.cramsan.edifikana.client.android.features.signin

import kotlin.random.Random

sealed class SignInActivityEvents {
    data object Noop : SignInActivityEvents()

    data class LaunchSignIn(
        val id: Int = Random.nextInt(),
    ) : SignInActivityEvents()

    data class CloseSignIn(
        val id: Int = Random.nextInt(),
    ) : SignInActivityEvents()
}
