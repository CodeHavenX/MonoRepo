package com.cramsan.edifikana.client.lib.navigation

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class EdifikanaPathNavigationTest {

    @Test
    fun `resolves PKCE reset deep link`() {
        assertIs<AuthDestination.SetNewPasswordDestination>(
            edifikanaResolveExternalUrl("edifikana://reset?code=CODE"),
        )
    }

    @Test
    fun `resolves implicit-flow reset deep link`() {
        assertIs<AuthDestination.SetNewPasswordDestination>(
            edifikanaResolveExternalUrl("edifikana://reset#access_token=abc&type=recovery"),
        )
    }

    @Test
    fun `resolves plain web path`() {
        assertIs<AuthDestination.SignInDestination>(edifikanaResolveExternalUrl("/auth/sign-in"))
    }

    @Test
    fun `returns null for unrecognized custom scheme`() {
        assertNull(edifikanaResolveExternalUrl("edifikana://other"))
    }
}
