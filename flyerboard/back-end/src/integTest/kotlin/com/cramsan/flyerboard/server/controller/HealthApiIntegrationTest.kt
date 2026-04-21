package com.cramsan.flyerboard.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.dependencyinjection.IntegTestFlyerControllerModule
import com.cramsan.flyerboard.server.dependencyinjection.IntegTestFlyerServiceModule
import com.cramsan.flyerboard.server.dependencyinjection.integTestFlyerApplicationModule
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration tests for [HealthController].
 *
 * Verifies that the health check endpoint returns the expected response.
 */
class HealthApiIntegrationTest : KoinTest {

    @BeforeTest
    fun setUp() {
        startTestKoin(
            integTestFlyerApplicationModule(),
            IntegTestFlyerControllerModule,
            IntegTestFlyerServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `GET api-v1-health returns 200 with ok status`() = testBackEndApplication {
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()

        val response = client.get("api/v1/health")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"message\""), "Expected 'message' field in response: $body")
        assertTrue(body.contains("\"ok\""), "Expected 'ok' message value in response: $body")
    }
}
