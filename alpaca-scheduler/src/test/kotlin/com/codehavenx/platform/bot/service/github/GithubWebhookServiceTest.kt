package com.codehavenx.platform.bot.service.github

import com.cramsan.framework.preferences.Preferences
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class GithubWebhookServiceTest {

    @MockK
    lateinit var preferences: Preferences

    lateinit var service: GithubWebhookService

    @BeforeTest
    fun setupTest() {
        MockKAnnotations.init(this) // turn relaxUnitFun on for all mocks

        service = GithubWebhookService(
            preferences,
        )
    }

    @Test
    fun `test registerWebhookEventToChannel`() {
        every { preferences.saveString("CHANNEL_ID_KEYPUSH", "12345") } just runs

        service.registerWebhookEventToChannel(WebhookEvent.PUSH, "12345")

        verify { preferences.saveString("CHANNEL_ID_KEYPUSH", "12345") }
    }


    @Test
    fun `test getWebhookEventChannel`() {
        every { preferences.loadString("CHANNEL_ID_KEYPUSH") } returns "12345"

        val channelId = service.getWebhookEventChannel(WebhookEvent.PUSH)

        assertEquals("12345", channelId)
    }
}