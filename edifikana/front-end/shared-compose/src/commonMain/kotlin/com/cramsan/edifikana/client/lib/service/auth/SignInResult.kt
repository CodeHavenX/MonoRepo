package com.cramsan.edifikana.client.lib.service.auth

/**
 * Result of a sign in operation.
 */
interface SignInResult {
    val success: Boolean
    val error: Throwable?
}
