package com.cramsan.flyerboard.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.dependencyinjection.IntegTestUserControllerModule
import com.cramsan.flyerboard.server.dependencyinjection.IntegTestUserServiceModule
import com.cramsan.flyerboard.server.dependencyinjection.integTestFlyerApplicationModule
import com.cramsan.flyerboard.server.service.UserService
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.utils.exceptions.ForbiddenException
import com.cramsan.framework.utils.file.readFileContent
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
 * Integration tests for [UserController].
 *
 * Tests the full HTTP request/response cycle for user operations using Ktor's test engine
 * and a mocked service layer.
 */
class UserApiIntegrationTest : KoinTest {

    @BeforeTest
    fun setUp() {
        startTestKoin(
            integTestFlyerApplicationModule(),
            IntegTestUserControllerModule,
            IntegTestUserServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `POST user without auth returns 401`() = testBackEndApplication {
        val requestBody = readFileContent("requests/create_user_request.json")
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.UnauthenticatedClientContext()

        val response = client.post("user") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST user with auth and successful createUser returns 200`() = testBackEndApplication {
        val requestBody = readFileContent("requests/create_user_request.json")
        val expectedResponse = readFileContent("requests/create_user_response.json")
        val userService = get<UserService>()
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = UserId("user123"), role = UserRole.USER),
        )
        coEvery {
            userService.createUser(
                userId = UserId("user123"),
                firstName = "John",
                lastName = "Doe",
            )
        } returns Result.success(
            User(
                id = UserId("user123"),
                firstName = "John",
                lastName = "Doe",
            ),
        )

        val response = client.post("user") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `POST user with auth and failing createUser returns error status`() = testBackEndApplication {
        val requestBody = readFileContent("requests/create_user_request.json")
        val userService = get<UserService>()
        val contextRetriever = get<ContextRetriever<FlyerBoardContextPayload>>()

        coEvery { contextRetriever.getContext(any()) } returns ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(userId = UserId("user123"), role = UserRole.USER),
        )
        coEvery {
            userService.createUser(
                userId = UserId("user123"),
                firstName = "John",
                lastName = "Doe",
            )
        } returns Result.failure(ForbiddenException("You are not allowed to create this user."))

        val response = client.post("user") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}
