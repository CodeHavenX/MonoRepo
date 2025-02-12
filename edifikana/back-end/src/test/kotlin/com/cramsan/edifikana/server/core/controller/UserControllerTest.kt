package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.auth.ClientContext
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.utils.readFileContent
import com.cramsan.framework.test.TestBase
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.coEvery
import io.mockk.mockk
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO: Add tests for phoneNumber & no email, and email & phoneNumber
class UserControllerTest : TestBase(), KoinTest {

    override fun setupTest() {
        startTestKoin()
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test createUser`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/create_user_request.json")
        val expectedResponse = readFileContent("requests/create_user_response.json")
        val userService = get<UserService>()
        coEvery {
            userService.createUser(
                email = "john.doe@example.com",
                phoneNumber = "",
                password = "password",
                firstName = "John",
                lastName = "Doe",
            )
        }.answers {
            Result.success(
                User(
                    id = UserId("user123"),
                    email = "john.doe@example.com",
                    phone = "",
                    firstName = "John",
                    lastName = "Doe",
                    hasGlobalPerms = false,
                )
            )
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        }

        // Act
        val response = client.post("user") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getUser`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_user_response.json")
        val userService = get<UserService>()
        coEvery {
            userService.getUser(UserId("user123"), false)
        }.answers {
            User(
                id = UserId("user123"),
                email = "john.doe@example.com",
                phone = "",
                firstName = "John",
                lastName = "Doe",
                hasGlobalPerms = false,
            )
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        }

        // Act
        val response = client.get("user/user123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getUsers`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_users_response.json")
        val userService = get<UserService>()
        coEvery {
            userService.getUsers()
        }.answers {
            listOf(
                User(
                    id = UserId("user123"),
                    email = "john.doe@example.com",
                    phone = "",
                    firstName = "John",
                    lastName = "Doe",
                    hasGlobalPerms = false,
                ),
                User(
                    id = UserId("user456"),
                    email = "jane.smith@example.com",
                    phone = "",
                    firstName = "Jane",
                    lastName = "Smith",
                    hasGlobalPerms = false,
                )
            )
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        }

        // Act
        val response = client.get("user")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateUser`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/update_user_request.json")
        val expectedResponse = readFileContent("requests/update_user_response.json")
        val userService = get<UserService>()
        coEvery {
            userService.updateUser(
                id = UserId("user123"),
                email = "updated.email@example.com"
            )
        }.answers {
            User(
                id = UserId("user123"),
                email = "updated.email@example.com",
                phone = "",
                firstName = "Updated",
                lastName = "Email",
                hasGlobalPerms = false,
            )
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        }

        // Act
        val response = client.put("user/user123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test deleteUser`() = testEdifikanaApplication {
        // Configure
        val userService = get<UserService>()
        coEvery {
            userService.deleteUser(UserId("user123"))
        }.answers {
            true
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        }

        // Act
        val response = client.delete("user/user123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
