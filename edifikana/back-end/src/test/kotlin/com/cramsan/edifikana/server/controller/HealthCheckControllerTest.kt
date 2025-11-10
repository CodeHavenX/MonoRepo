package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
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
        startTestKoin(
            createJson(),
            TestControllerModule,
            TestServiceModule,
        )
    }

    /**
     * Clean up the test.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test health check for an unauthenticated user`() = testBackEndApplication {
        // Configure
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.UnauthenticatedClientContext()
        }

        // Act
        val response = client.get("health")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test health check for an authenticated user`() = testBackEndApplication {
        // Configure
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                SupabaseContextPayload(
                    userId = UserId("test-user-id"),
                    userInfo = mockk()
                )
            )
        }

        // Act
        val response = client.get("health")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
