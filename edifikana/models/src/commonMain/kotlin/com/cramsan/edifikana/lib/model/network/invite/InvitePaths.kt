package com.cramsan.edifikana.lib.model.network.invite

/**
 * Canonical web path of the invitation landing screen, reached via the link in an invitation
 * email when no session exists yet. Shared between the back-end redirect URL and the front-end's
 * [com.cramsan.framework.annotations.WebPath] annotation so the two values can never drift apart.
 */
const val INVITE_ACCEPT_WEB_PATH = "/auth/invite"

/**
 * Canonical web path of the invitation accept/decline screen, reached once a session exists
 * (already signed in, or immediately after sign-up/sign-in from [INVITE_ACCEPT_WEB_PATH]).
 */
const val INVITE_ACCEPT_CONFIRM_WEB_PATH = "/auth/invite/confirm"
