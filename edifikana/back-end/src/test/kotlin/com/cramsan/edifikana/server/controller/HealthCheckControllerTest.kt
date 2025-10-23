package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.controller.authentication.ClientContext
import com.cramsan.edifikana.server.controller.authentication.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
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
    fun `test health check for an unauthenticated user`() = testEdifikanaApplication {
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

    @Test
    fun `test health check for an authenticated user`() = testEdifikanaApplication {
        // Configure
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userId = UserId("test-user-id"),
                userInfo = mockk()
            )
        }

        // Act
        val response = client.get("health")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
