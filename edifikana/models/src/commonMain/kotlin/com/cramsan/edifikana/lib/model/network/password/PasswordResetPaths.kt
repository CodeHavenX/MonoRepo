package com.cramsan.edifikana.lib.model.network.password

/**
 * Canonical web path of the set-new-password screen, reached via the link in a password-reset
 * email. Shared between the back-end's password-reset redirect URL and the front-end's
 * `WebPath` annotation so the two values can never drift apart.
 */
const val SET_NEW_PASSWORD_WEB_PATH = "/auth/set-new-password"
