package com.cramsan.edifikana.client.lib.service.auth

interface SignInResult {
    val success: Boolean
    val error: Throwable?
}
