package com.cramsan.edifikana.client.lib.features.settings

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class SettingsDestinationTest {

    @Test
    fun `fromWebPath returns GeneralSettingsDestination`() {
        assertIs<SettingsDestination.GeneralSettingsDestination>(SettingsDestination.fromWebPath("/settings"))
    }

    @Test
    fun `fromWebPath returns null for unrecognized path`() {
        assertNull(SettingsDestination.fromWebPath("/unknown"))
    }
}
