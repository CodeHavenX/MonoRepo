package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class HealthCheckControllerTest : CoroutineTest(), KoinTest {

    /**
     * Setup the test.
     */
    @BeforeTest
    fun setupTest() {
        startTestKoin()
    }

    /**
     * Clean up the test.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test health check for an unauthenticated user`() = testTemplateReplaceMeApplication {
        // Configure
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.UnauthenticatedClientContext
        }

        // Act
        val response = client.get("health")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
