package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.edifikana.server.core.controller.auth.ClientContext
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.utils.readFileContent
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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserControllerTest : TestBase(), KoinTest {

    @BeforeTest
    fun setupTest() {
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
                phoneNumber = "5051352468",
                password = "password",
                firstName = "John",
                lastName = "Doe",
                authorizeOtp = false,
            )
        }.answers {
            Result.success(
                User(
                    id = UserId("user123"),
                    email = "john.doe@example.com",
                    phoneNumber = "5051352468",
                    firstName = "John",
                    lastName = "Doe",
                    isVerified = false,
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

    /**
     * Test to verify that createUser throws an exception when an unknown error occurs.
     * This test simulates an unexpected error during the user creation process.
     */
    @Test
    fun `test createUser throws exception when unknown error occurs`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/create_user_request.json")
        val userService = get<UserService>()
        coEvery {
            userService.createUser(
                email = "john.doe@example.com",
                phoneNumber = "5051352468",
                password = "password",
                firstName = "John",
                lastName = "Doe",
                authorizeOtp = false,
            )
        }.answers {
            Result.failure(RuntimeException("There was an unexpected error."))
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
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("There was an unexpected error."))
    }

    /**
     * Test to verify that createUser throws an exception when the user already exists.
     * This test simulates a conflict error during the user creation process.
     */
    @Test
    fun `test createUser throws exception when user already exists`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/create_user_request.json")
        val userService = get<UserService>()
        coEvery {
            userService.createUser(
                email = "john.doe@example.com",
                phoneNumber = "5051352468",
                password = "password",
                firstName = "John",
                lastName = "Doe",
                authorizeOtp = false,
            )
        }.answers {
            Result.failure(ClientRequestExceptions.ConflictException("Error: User with this email already exists."))
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
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertTrue(response.bodyAsText().contains("Error: User with this email already exists."))
    }

    @Test
    fun `test getUser`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_user_response.json")
        val userService = get<UserService>()
        coEvery {
            userService.getUser(UserId("user123"))
        }.answers {
            User(
                id = UserId("user123"),
                email = "john.doe@example.com",
                phoneNumber = "5051352468",
                firstName = "John",
                lastName = "Doe",
                isVerified = false,

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
                    phoneNumber = "5051352468",
                    firstName = "John",
                    lastName = "Doe",
                    isVerified = false,
                ),
                User(
                    id = UserId("user456"),
                    email = "jane.smith@example.com",
                    phoneNumber = "5051352469",
                    firstName = "Jane",
                    lastName = "Smith",
                    isVerified = false,
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
                phoneNumber = "5051382468",
                firstName = "Updated",
                lastName = "Email",
                isVerified = false,
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
