package com.codehavenx.platform.bot.controller.webhook

import com.codehavenx.platform.bot.config.createJson
import com.codehavenx.platform.bot.controller.kord.DiscordController
import com.codehavenx.platform.bot.di.createApplicationModule
import com.codehavenx.platform.bot.di.createFrameworkModule
import com.codehavenx.platform.bot.network.gh.CodePushPayload
import com.codehavenx.platform.bot.service.github.GithubWebhookService
import com.codehavenx.platform.bot.service.github.WebhookEvent
import com.codehavenx.platform.bot.utils.readResource
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.test.TestBase
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class GithubCommitPushEntryPointTest : TestBase(){

    private lateinit var entryPoint: GithubCommitPushEntryPoint

    private val json = createJson()

    @MockK
    private lateinit var githubWebhookService: GithubWebhookService

    @MockK
    private lateinit var discordController: DiscordController
    @BeforeTest
    override fun setupTest() {
        MockKAnnotations.init(this) // turn relaxUnitFun on for all mocks
        EventLogger.setInstance(mockk(relaxed = true))

        entryPoint = GithubCommitPushEntryPoint(
            githubWebhookService,
            discordController,
        )
    }

    @Test
    fun `test path` () {
        assertEquals("github/push", entryPoint.path)
    }

    @Test
    fun `test onPayload` () = runBlockingTest {
        val channelId = "channelId"
        val payload: CodePushPayload = json.decodeFromString(readResource("commit_push.json")!!)
        every { githubWebhookService.getWebhookEventChannel(WebhookEvent.PUSH) } returns channelId
        val capturer = slot<UserMessageCreateBuilder.() -> Unit>()
        coEvery { discordController.sendMessage(any(), capture(capturer)) } just runs

        val builder: UserMessageCreateBuilder = mockk(relaxed = true)

        val response = entryPoint.onPayload(payload)

        val discordMessage = capturer.captured
        builder.discordMessage()

        coVerify { discordController.sendMessage(eq(channelId), any()) }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Event handled", response.body)
        verify {
            builder.content = "CRamsan pushed a commit to MonoRepo\n[DBP] Fixing date parsing\nhttps://github.com/CodeHavenX/MonoRepo/commit/3695c9218ac300065f38cf5c74b82690c0bd6d04"
        }
    }

    @Test
    fun `test onPayload with no channelId` () = runBlockingTest {
        val payload: CodePushPayload = json.decodeFromString(readResource("commit_push.json")!!)
        every { githubWebhookService.getWebhookEventChannel(WebhookEvent.PUSH) } returns null

        val response = entryPoint.onPayload(payload)

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals("Event unhandled", response.body)
    }

}