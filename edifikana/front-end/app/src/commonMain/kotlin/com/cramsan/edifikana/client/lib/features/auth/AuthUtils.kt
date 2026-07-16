package com.cramsan.edifikana.client.lib.features.auth

import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.lib.model.invite.InviteId

/**
 * Helper function to handle the routing after a user is authenticated but we need to determine their next location.
 *
 * If there is an [inviteId], we can assume we are in an invite flow so we route the user to the
 * [AuthDestination.InvitationAcceptConfirmDestination].
 * If in a non-invite flow we need to determine if the user has completed their onboarding(being part of at least
 * one org). If they are part of an org, send them to the homescreen, otherwise open the
 * [AuthDestination.SelectOrgDestination].
 */
suspend fun postAuthenticationDestination(
    organizationManager: OrganizationManager,
    inviteId: InviteId?,
): EdifikanaWindowsEvent {
    if (inviteId != null) {
        return EdifikanaWindowsEvent.NavigateToScreen(
            AuthDestination.InvitationAcceptConfirmDestination(inviteId),
            clearTop = true,
        )
    }

    val organizations = organizationManager.getOrganizations().getOrNull()

    return if (organizations.isNullOrEmpty()) {
        EdifikanaWindowsEvent.NavigateToScreen(
            AuthDestination.SelectOrgDestination,
            clearTop = true,
        )
    } else {
        EdifikanaWindowsEvent.NavigateToNavGraph(
            EdifikanaNavGraphDestination.HomeNavGraphDestination,
            clearTop = true,
        )
    }
}
