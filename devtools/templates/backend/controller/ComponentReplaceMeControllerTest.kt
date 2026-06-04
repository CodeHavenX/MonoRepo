package com.cramsan.templatereplaceme.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.file.readFileContent
import com.cramsan.templatereplaceme.lib.model.ComponentReplaceMeId
import com.cramsan.templatereplaceme.lib.serialization.createJson
import com.cramsan.templatereplaceme.server.dependencyinjection.TestControllerModule
import com.cramsan.templatereplaceme.server.dependencyinjection.TestServiceModule
import com.cramsan.templatereplaceme.server.dependencyinjection.testApplicationModule
import com.cramsan.templatereplaceme.server.service.ComponentReplaceMeService
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceMe
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

/**
 * Integration test for [ComponentReplaceMeController].
 *
 * Tests the full HTTP layer end-to-end using a test Ktor application. The service is mocked
 * so this test exercises only routing, request parsing, and response serialization — not
 * business logic (that lives in [ComponentReplaceMeServiceTest]).
 *
 * Request/response JSON fixtures live in:
 *   `back-end/src/test/resources/requests/componentreplaceme_request.json`
 *   `back-end/src/test/resources/requests/componentreplaceme_response.json`
 *
 * TODO: Add one test per API operation in [ComponentReplaceMeApi]. Follow the
 *       Arrange / Act / Assert pattern shown in `test create` below.
 * TODO: Add tests for error cases (service throws, invalid request body, auth failure).
 * TODO: Register this controller and its service mock in TestModule.kt:
 *       - In TestControllerModule: `singleOf(::ComponentReplaceMeController) { bind<Controller>() }`
 *       - In TestServiceModule:    `single<ComponentReplaceMeService> { mockk() }`
 */
class ComponentReplaceMeControllerTest :
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
    fun `test create`() =
        testBackEndApplication {
            // Arrange
            val requestBody = readFileContent("requests/componentreplaceme_request.json")
            val expectedResponse = readFileContent("requests/componentreplaceme_response.json")
            val componentreplacemeService = get<ComponentReplaceMeService>()
            coEvery {
                componentreplacemeService.create(id = "test-id")
            }.answers {
                Result.success(ComponentReplaceMe(id = ComponentReplaceMeId("test-id")))
            }
            val contextRetriever = get<ContextRetriever<Unit>>()
            coEvery {
                contextRetriever.getContext(any())
            }.answers {
                ClientContext.UnauthenticatedClientContext()
            }

            // Act
            val response =
                client.post("componentreplaceme") {
                    setBody(requestBody)
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedResponse, response.bodyAsText())
        }
}
