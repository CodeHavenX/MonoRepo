package com.cramsan.flyerboard.client.lib.features.auth

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class AuthDestinationTest {

    @Test
    fun `fromWebPath returns SignInDestination for sign-in path`() {
        assertIs<AuthDestination.SignInDestination>(AuthDestination.fromWebPath("/sign-in"))
    }

    @Test
    fun `fromWebPath returns SignUpDestination for sign-up path`() {
        assertIs<AuthDestination.SignUpDestination>(AuthDestination.fromWebPath("/sign-up"))
    }

    @Test
    fun `fromWebPath returns null for unrecognized path`() {
        assertNull(AuthDestination.fromWebPath("/unknown"))
    }

    @Test
    fun `fromWebPath returns null for empty path`() {
        assertNull(AuthDestination.fromWebPath(""))
    }
}
