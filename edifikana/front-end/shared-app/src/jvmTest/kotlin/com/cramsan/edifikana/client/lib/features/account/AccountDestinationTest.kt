package com.cramsan.edifikana.client.lib.features.account

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class AccountDestinationTest {

    @Test
    fun `fromWebPath returns MyAccountDestination`() {
        assertIs<AccountDestination.MyAccountDestination>(AccountDestination.fromWebPath("/account"))
    }

    @Test
    fun `fromWebPath returns NotificationsDestination`() {
        assertIs<AccountDestination.NotificationsDestination>(AccountDestination.fromWebPath("/account/notifications"))
    }

    @Test
    fun `fromWebPath returns ChangePasswordDestination`() {
        assertIs<AccountDestination.ChangePasswordDestination>(AccountDestination.fromWebPath("/account/change-password"))
    }

    @Test
    fun `fromWebPath returns null for unrecognized path`() {
        assertNull(AccountDestination.fromWebPath("/unknown"))
    }
}
