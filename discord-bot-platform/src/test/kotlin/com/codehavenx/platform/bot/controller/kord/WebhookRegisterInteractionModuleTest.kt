package com.codehavenx.platform.bot.controller.kord

import com.codehavenx.platform.bot.controller.kord.modules.WebhookRegisterInteractionModule
import com.codehavenx.platform.bot.service.github.GithubWebhookService
import com.codehavenx.platform.bot.service.github.WebhookEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.test.TestBase
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class WebhookRegisterInteractionModuleTest : TestBase() {

    @MockK
    lateinit var githubWebhookService: GithubWebhookService

    lateinit var module: InteractionModule

    override fun setupTest() {
        MockKAnnotations.init(this) // turn relaxUnitFun on for all mocks
        EventLogger.setInstance(mockk(relaxed = true))
        module = WebhookRegisterInteractionModule(
            githubWebhookService,
        )
    }

    @Test
    fun `verify command`() {
        assertEquals("wh_register", module.command)
    }

    @Test
    fun `verify description`() {
        assertEquals("Register a webhook to the current channel", module.description)
    }

    @Test
    fun `verify onGlobalChatInputRegister`() = runBlockingTest {
        val inputRegister = module.onGlobalChatInputRegister()

        val builder = GlobalChatInputCreateBuilderAdapter()
        inputRegister.invoke(builder)

        assertEquals(1, builder.options?.size)
        val eventArg = builder.options?.first()
        assertEquals("event", eventArg?.name)
        assertEquals(true, eventArg?.required)
    }

    @Test
    fun `verify onGlobalChatInteraction`() = runBlockingTest {
        val interaction: GuildChatInputCommandInteraction = mockk()
        val interactionCommand: InteractionCommand = mockk()
        val stringsParams = mapOf(
            "event" to "WORKFLOW_JOB",
        )
        val deferredResponse: DeferredPublicMessageInteractionResponseBehavior = mockk()
        val channelId = Snowflake(Instant.fromEpochMilliseconds(10000))

        every { interaction.command } returns interactionCommand
        every { interactionCommand.strings } returns stringsParams
        coEvery { interaction.deferPublicResponse() } returns deferredResponse
        every { interaction.channelId } returns channelId
        every {
            githubWebhookService.registerWebhookEventToChannel(
                WebhookEvent.WORKFLOW_JOB,
                channelId.toString(),
            )
        } just runs
        val builder: InteractionResponseModifyBuilder = mockk(relaxed = true)

        val response = module.onGlobalChatInteraction(interaction)
        builder.response()

        verify {
            builder.content = "This channel will now receive events of type WORKFLOW_JOB"
        }
    }
}
