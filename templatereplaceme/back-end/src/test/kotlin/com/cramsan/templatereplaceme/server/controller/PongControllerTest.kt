package com.cramsan.templatereplaceme.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.file.readFileContent
import com.cramsan.templatereplaceme.lib.model.PingPong
import com.cramsan.templatereplaceme.lib.serialization.createJson
import com.cramsan.templatereplaceme.server.dependencyinjection.TestControllerModule
import com.cramsan.templatereplaceme.server.dependencyinjection.TestServiceModule
import com.cramsan.templatereplaceme.server.dependencyinjection.testApplicationModule
import com.cramsan.templatereplaceme.server.service.PingPongService
import com.cramsan.templatereplaceme.server.service.models.Pong
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.coEvery
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PongControllerTest :
    CoroutineTest(),
    KoinTest {
    @BeforeTest
    fun setupTest() {
        startTestKoin(
            testApplicationModule(createJson()),
            TestControllerModule,
            TestServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test ping`() =
        testBackEndApplication {
            // Arrange
            val requestBody = readFileContent("requests/ping_request.json")
            val expectedResponse = readFileContent("requests/pong_response.json")
            val pingPongService = get<PingPongService>()
            coEvery {
                pingPongService.ping(
                    firstName = "John",
                    lastName = "Doe",
                )
            }.answers {
                Result.success(
                    Pong(
                        id = PingPong("user123"),
                        firstName = "John",
                        lastName = "Doe",
                    ),
                )
            }
            val contextRetriever = get<ContextRetriever<Unit>>()
            coEvery {
                contextRetriever.getContext(any())
            }.answers {
                ClientContext.UnauthenticatedClientContext()
            }

            // Act
            val response =
                client.post("ping") {
                    setBody(requestBody)
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedResponse, response.bodyAsText())
        }
}
