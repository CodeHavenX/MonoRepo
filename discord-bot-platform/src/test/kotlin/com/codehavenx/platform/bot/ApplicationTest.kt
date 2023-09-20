package com.codehavenx.platform.bot

import com.codehavenx.platform.bot.config.createJson
import com.codehavenx.platform.bot.controller.kord.DiscordController
import com.codehavenx.platform.bot.controller.webhook.GithubCommitPushEntryPoint
import com.codehavenx.platform.bot.di.createApplicationModule
import com.codehavenx.platform.bot.di.createFrameworkModule
import com.codehavenx.platform.bot.ktor.HttpResponse
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
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest : TestBase() {

    @MockK(relaxed = true)
    private lateinit var githubCommitPushEntryPoint: GithubCommitPushEntryPoint

    @MockK(relaxed = true)
    private lateinit var githubWebhookService: GithubWebhookService

    @MockK(relaxed = true)
    private lateinit var discordController: DiscordController

    private val json = createJson()
    override fun setupTest() { }

    @Test
    fun `test onPayload with no channelId` () = testApplication {
        MockKAnnotations.init(this) // turn relaxUnitFun on for all mocks

        githubCommitPushEntryPoint = spyk(GithubCommitPushEntryPoint(
            githubWebhookService,
            discordController,
        ))

        coEvery { githubCommitPushEntryPoint.onPayload(any()) } returns HttpResponse(
            HttpStatusCode.OK,
            "Success!"
        )

        val client = createClient {
            install(ContentNegotiation) {
                json(createJson())
            }
        }

        startKoin {
            modules(
                createFrameworkModule(),
                createApplicationModule(
                    githubCommitPushEntryPoint = githubCommitPushEntryPoint,
                    githubWebhookService = githubWebhookService,
                    discordController = discordController,
                ),
            )
        }

        val payload: CodePushPayload = json.decodeFromString(readResource("commit_push.json")!!)

        val response = client.post("/webhook/github/push") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        assertEquals("Success!", response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)

        stopKoin()
    }
}