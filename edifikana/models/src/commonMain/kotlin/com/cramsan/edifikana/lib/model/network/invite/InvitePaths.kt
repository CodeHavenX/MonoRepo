package com.cramsan.edifikana.lib.model.network.invite

/**
 * Canonical web path of the invitation accept screen, reached via the link in an invitation
 * email. Shared between the back-end redirect URL and the front-end's [com.cramsan.framework.annotations.WebPath]
 * annotation so the two values can never drift apart.
 */
const val INVITE_ACCEPT_WEB_PATH = "/auth/invite"
