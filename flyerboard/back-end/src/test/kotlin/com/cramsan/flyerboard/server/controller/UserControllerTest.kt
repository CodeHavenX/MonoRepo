package com.cramsan.flyerboard.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.serialization.createJson
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.dependencyinjection.TestControllerModule
import com.cramsan.flyerboard.server.dependencyinjection.TestServiceModule
import com.cramsan.flyerboard.server.dependencyinjection.testApplicationModule
import com.cramsan.flyerboard.server.service.UserService
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.exceptions.ForbiddenException
import com.cramsan.framework.utils.file.readFileContent
import io.ktor.client.request.get
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

class UserControllerTest :
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
    fun `test createUser`() =
        testBackEndApplication { client ->
            // Arrange
            val requestBody = readFileContent("requests/create_user_request.json")
            val expectedResponse = readFileContent("requests/create_user_response.json")
            val userService = get<UserService>()
            coEvery {
                userService.createUser(
                    userId = UserId("user123"),
                    firstName = "John",
                    lastName = "Doe",
                )
            }.answers {
                User(
                    id = UserId("user123"),
                    firstName = "John",
                    lastName = "Doe",
                )
            }
            val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
            coEvery {
                contextRetriever.getContext(any())
            }.answers {
                ClientContext.AuthenticatedClientContext(
                    FlyerBoardContextPayload(userId = UserId("user123"), role = UserRole.USER),
                )
            }

            // Act
            val response =
                client.post("api/v1/user") {
                    setBody(requestBody)
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedResponse, response.bodyAsText())
        }

    @Test
    fun `test getCurrentUser`() =
        testBackEndApplication { client ->
            // Arrange
            val expectedResponse = readFileContent("requests/get_current_user_response.json")
            val userService = get<UserService>()
            coEvery {
                userService.getUser(UserId("user123"))
            }.answers {
                User(
                    id = UserId("user123"),
                    firstName = "John",
                    lastName = "Doe",
                )
            }
            val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
            coEvery {
                contextRetriever.getContext(any())
            }.answers {
                ClientContext.AuthenticatedClientContext(
                    FlyerBoardContextPayload(userId = UserId("user123"), role = UserRole.ADMIN),
                )
            }

            // Act
            val response = client.get("api/v1/user/me")

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedResponse, response.bodyAsText())
        }

    @Test
    fun `test getCurrentUser fails with 401 when unauthenticated`() =
        testBackEndApplication { client ->
            // Arrange
            val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
            coEvery {
                contextRetriever.getContext(any())
            }.answers {
                ClientContext.UnauthenticatedClientContext()
            }

            // Act
            val response = client.get("api/v1/user/me")

            // Assert
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun `test createUser fails with 401 when unauthenticated`() =
        testBackEndApplication { client ->
            // Arrange
            val requestBody = readFileContent("requests/create_user_request.json")
            val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
            coEvery {
                contextRetriever.getContext(any())
            }.answers {
                ClientContext.UnauthenticatedClientContext()
            }

            // Act
            val response =
                client.post("api/v1/user") {
                    setBody(requestBody)
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun `test createUser fails when service returns failure`() =
        testBackEndApplication { client ->
            // Arrange
            val requestBody = readFileContent("requests/create_user_request.json")
            val userService = get<UserService>()
            coEvery {
                userService.createUser(
                    userId = UserId("user123"),
                    firstName = "John",
                    lastName = "Doe",
                )
            } throws ForbiddenException("You are not allowed to create this user.")
            val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()
            coEvery {
                contextRetriever.getContext(any())
            }.answers {
                ClientContext.AuthenticatedClientContext(
                    FlyerBoardContextPayload(userId = UserId("user123"), role = UserRole.USER),
                )
            }

            // Act
            val response =
                client.post("api/v1/user") {
                    setBody(requestBody)
                    contentType(ContentType.Application.Json)
                }

            // Assert
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
}
