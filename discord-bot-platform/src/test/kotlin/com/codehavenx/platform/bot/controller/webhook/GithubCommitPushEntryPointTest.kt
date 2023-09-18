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
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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

    @MockK(
        relaxed = true,
    )
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

    @AfterTest
    fun closeTest() {

    }

    @Test
    fun `test path` () {
        assertEquals("github/push", entryPoint.path)
    }

    @Test
    fun `test onPayload` () = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json(createJson())
            }
        }

        startKoin {
            modules(
                createFrameworkModule(),
                createApplicationModule(
                    discordController = discordController,
                    githubWebhookService = githubWebhookService,
                    githubCommitPushEntryPoint = entryPoint,
                ),
            )
        }

        val channelId = "channelId"
        val payload: CodePushPayload = json.decodeFromString(readResource("commit_push.json")!!)
        every { githubWebhookService.getWebhookEventChannel(WebhookEvent.PUSH) } returns channelId

        val response = client.post("/webhook/github/push") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        coVerify { discordController.sendMessage(channelId, any()) }
        assertEquals("Event handled", response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)

        stopKoin()
    }

    @Test
    fun `test onPayload with no channelId` () = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json(createJson())
            }
        }

        startKoin {
            modules(
                createFrameworkModule(),
                createApplicationModule(
                    discordController = discordController,
                    githubWebhookService = githubWebhookService,
                    githubCommitPushEntryPoint = entryPoint,
                ),
            )
        }

        val payload: CodePushPayload = json.decodeFromString(readResource("commit_push.json")!!)
        every { githubWebhookService.getWebhookEventChannel(WebhookEvent.PUSH) } returns null

        val response = client.post("/webhook/github/push") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        coVerify(exactly = 0) { discordController.sendMessage(any(), any()) }
        assertEquals("Event unhandled", response.bodyAsText())
        assertEquals(HttpStatusCode.InternalServerError, response.status)

        stopKoin()
    }

}