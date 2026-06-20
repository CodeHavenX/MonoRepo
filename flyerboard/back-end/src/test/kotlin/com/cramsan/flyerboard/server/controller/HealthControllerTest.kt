package com.cramsan.flyerboard.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.flyerboard.lib.model.network.HealthCheckNetworkResponse
import com.cramsan.flyerboard.lib.serialization.createJson
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.dependencyinjection.TestFlyerControllerModule
import com.cramsan.flyerboard.server.dependencyinjection.TestFlyerServiceModule
import com.cramsan.flyerboard.server.dependencyinjection.testApplicationModule
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import kotlinx.serialization.decodeFromString
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [HealthController].
 */
class HealthControllerTest :
    CoroutineTest(),
    KoinTest {
    @BeforeTest
    fun setupTest() {
        startTestKoin(
            testApplicationModule(createJson()),
            TestFlyerControllerModule,
            TestFlyerServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test check returns ok status without requiring authentication`() =
        testBackEndApplication {
            val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
            coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()

            val response = client.get("api/v1/health")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = createJson().decodeFromString<HealthCheckNetworkResponse>(response.bodyAsText())
            assertEquals("ok", body.message)
        }
}
