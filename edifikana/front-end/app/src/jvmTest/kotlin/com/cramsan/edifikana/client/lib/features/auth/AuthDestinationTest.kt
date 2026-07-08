package com.cramsan.edifikana.client.lib.features.auth

import com.cramsan.edifikana.lib.model.invite.InviteId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AuthDestinationTest {

    @Test
    fun `fromWebPath returns SignInDestination`() {
        assertIs<AuthDestination.SignInDestination>(AuthDestination.fromWebPath("/auth/sign-in"))
    }

    @Test
    fun `fromWebPath returns SignUpDestination with decoded percent-encoded value`() {
        val destination =
            assertIs<AuthDestination.SignUpDestination>(
                AuthDestination.fromWebPath("/auth/sign-up?userEmail=test%40example.com"),
            )
        assertEquals("test@example.com", destination.userEmail)
    }

    @Test
    fun `fromWebPath returns ValidationDestination`() {
        assertIs<AuthDestination.ValidationDestination>(
            AuthDestination.fromWebPath("/auth/validation?userEmail=test%40example.com&accountCreationFlow=true")
        )
    }

    @Test
    fun `fromWebPath returns SelectOrgDestination`() {
        assertIs<AuthDestination.SelectOrgDestination>(AuthDestination.fromWebPath("/auth/select-org"))
    }

    @Test
    fun `fromWebPath returns CreateNewOrgDestination`() {
        assertIs<AuthDestination.CreateNewOrgDestination>(AuthDestination.fromWebPath("/auth/create-org"))
    }

    @Test
    fun `fromWebPath returns PasswordResetDestination without optional param`() {
        assertIs<AuthDestination.PasswordResetDestination>(AuthDestination.fromWebPath("/auth/password-reset"))
    }

    @Test
    fun `fromWebPath returns PasswordResetDestination with optional param`() {
        assertIs<AuthDestination.PasswordResetDestination>(
            AuthDestination.fromWebPath("/auth/password-reset?prefillEmail=test%40example.com")
        )
    }

    @Test
    fun `fromWebPath returns PasswordResetConfirmationDestination`() {
        assertIs<AuthDestination.PasswordResetConfirmationDestination>(
            AuthDestination.fromWebPath("/auth/password-reset-confirm?userEmail=test%40example.com")
        )
    }

    @Test
    fun `fromWebPath returns null for unrecognized path`() {
        assertNull(AuthDestination.fromWebPath("/unknown"))
    }

    @Test
    fun `fromWebPath returns null for sign-up path without required param`() {
        assertNull(AuthDestination.fromWebPath("/auth/sign-up"))
    }

    @Test
    fun `fromWebPath returns SetNewPasswordDestination`() {
        assertIs<AuthDestination.SetNewPasswordDestination>(AuthDestination.fromWebPath("/auth/set-new-password#access_token=token&expires_at=1783027428&expires_in=3600&refresh_token=f7ei5f5ctfnw&sb=&token_type=bearer&type=recovery"))
    }

    @Test
    fun `fromWebPath returns InvitationAcceptDestination`() {
        val destination =
            assertIs<AuthDestination.InvitationAcceptDestination>(
                AuthDestination.fromWebPath("/auth/invite?inviteId=abc123"),
            )
        assertEquals(InviteId("abc123"), destination.inviteId)
    }

    @Test
    fun `fromWebPath returns InvitationAcceptConfirmDestination`() {
        val destination =
            assertIs<AuthDestination.InvitationAcceptConfirmDestination>(
                AuthDestination.fromWebPath("/auth/invite/confirm?inviteId=abc123"),
            )
        assertEquals(InviteId("abc123"), destination.inviteId)
    }
}
