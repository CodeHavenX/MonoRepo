package com.cramsan.flyerboard.client.lib.features.main

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MainDestinationTest {

    @Test
    fun `fromWebPath returns FlyerListDestination for root path`() {
        assertIs<MainDestination.FlyerListDestination>(MainDestination.fromWebPath("/"))
    }

    @Test
    fun `fromWebPath returns FlyerDetailDestination for flyer path with id`() {
        val result = MainDestination.fromWebPath("/flyer?flyerId=abc123")
        assertIs<MainDestination.FlyerDetailDestination>(result)
        assertNotNull(result)
    }

    @Test
    fun `fromWebPath returns MyFlyersDestination for my-flyers path`() {
        assertIs<MainDestination.MyFlyersDestination>(MainDestination.fromWebPath("/my-flyers"))
    }

    @Test
    fun `fromWebPath returns ArchiveDestination for archive path`() {
        assertIs<MainDestination.ArchiveDestination>(MainDestination.fromWebPath("/archive"))
    }

    @Test
    fun `fromWebPath returns ModerationQueueDestination for moderation path`() {
        assertIs<MainDestination.ModerationQueueDestination>(MainDestination.fromWebPath("/moderation"))
    }

    @Test
    fun `fromWebPath returns FlyerEditDestination for edit path with id`() {
        val result = MainDestination.fromWebPath("/my-flyers/edit?flyerId=abc123")
        assertIs<MainDestination.FlyerEditDestination>(result)
    }

    @Test
    fun `fromWebPath returns FlyerSubmitDestination for submit path`() {
        assertIs<MainDestination.FlyerSubmitDestination>(MainDestination.fromWebPath("/submit"))
    }

    @Test
    fun `fromWebPath returns null for unrecognized path`() {
        assertNull(MainDestination.fromWebPath("/unknown"))
    }

    @Test
    fun `fromWebPath returns null for flyer path without required id`() {
        assertNull(MainDestination.fromWebPath("/flyer"))
    }
}
