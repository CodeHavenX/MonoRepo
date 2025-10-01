package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.edifikana.server.core.service.authorization.RBACService
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserControllerTest : CoroutineTest(), KoinTest {

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
        // Arrange
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
            )
        }.answers {
            Result.success(
                User(
                    id = UserId("user123"),
                    email = "john.doe@example.com",
                    phoneNumber = "5051352468",
                    firstName = "John",
                    lastName = "Doe",
                    authMetadata = null,
                    role = UserRole.USER,
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
        // Arrange
        val requestBody = readFileContent("requests/create_user_request.json")
        val userService = get<UserService>()
        coEvery {
            userService.createUser(
                email = "john.doe@example.com",
                phoneNumber = "5051352468",
                password = "password",
                firstName = "John",
                lastName = "Doe",
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
        // Arrange
        val requestBody = readFileContent("requests/create_user_request.json")
        val userService = get<UserService>()
        coEvery {
            userService.createUser(
                email = "john.doe@example.com",
                phoneNumber = "5051352468",
                password = "password",
                firstName = "John",
                lastName = "Doe",
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
    fun `test getUser passes when user is requesting info on self`() = testEdifikanaApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_user_response.json")
        val userService = get<UserService>()
        val rbacService = get<RBACService>()
        val userId = UserId("user123")
        coEvery {
            userService.getUser(userId)
        }.answers {
            Result.success(
                User(
                    id = UserId("user123"),
                    email = "john.doe@example.com",
                    phoneNumber = "5051352468",
                    firstName = "John",
                    lastName = "Doe",
                    authMetadata = null,
                    role = UserRole.EMPLOYEE,
                )
            )
        }
        val contextRetriever = get<ContextRetriever>()
        val context = ClientContext.AuthenticatedClientContext(
            userInfo = mockk(),
            userId = UserId("user123"),
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRole(context, userId)
        }.answers {
            true
        }

        // Act
        val response = client.get("user/user123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getUser fails when user requests user data for another user`() = testEdifikanaApplication {
        // Arrange
        val expectedResponse = "You are not authorized to perform this action."
        val userService = get<UserService>()
        val rbacService = get<RBACService>()
        val userID = UserId("user654")
        val targetUserId = UserId("user123")
        val contextRetriever = get<ContextRetriever>()

        val context = ClientContext.AuthenticatedClientContext(
            userInfo = mockk(),
            userId = userID
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRole(context, targetUserId)
        }.answers {
            false
        }

        // Act
        val response = client.get("user/user123")

        // Assert
        coVerify { userService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getUsers passes when user has required role`() = testEdifikanaApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_users_response.json")
        val userService = get<UserService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")

        coEvery {
            userService.getUsers(orgId)
        }.answers {
            Result.success(
                listOf(
                    User(
                        id = UserId("user123"),
                        email = "john.doe@example.com",
                        phoneNumber = "5051352468",
                        firstName = "John",
                        lastName = "Doe",
                        authMetadata = null,
                        role = UserRole.EMPLOYEE,
                    ),
                    User(
                        id = UserId("user456"),
                        email = "jane.smith@example.com",
                        phoneNumber = "5051352469",
                        firstName = "Jane",
                        lastName = "Smith",
                        authMetadata = null,
                        role = UserRole.EMPLOYEE

                    )
                )
            )
        }
        val contextRetriever = get<ContextRetriever>()
        val context = ClientContext.AuthenticatedClientContext(
            userInfo = mockk(),
            userId = UserId("user123"),
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER)
        }.answers {
            true
        }

        // Act
        val response = client.get("user?orgId=org123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getUsers fails when the user does NOT have the required role`() = testEdifikanaApplication {
        // Arrange
        val expectedResponse = "You are not authorized to perform this action."
        val userService = get<UserService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever>()
        val context = ClientContext.AuthenticatedClientContext(
            userInfo = mockk(),
            userId = UserId("user123"),
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER)
        }.answers {
            false
        }

        // Act
        val response = client.get("user?orgId=org123")

        // Assert
        coVerify { userService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateUser succeeds when user is updating self`() = testEdifikanaApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_user_request.json")
        val expectedResponse = readFileContent("requests/update_user_response.json")
        val userService = get<UserService>()
        val rbacService = get<RBACService>()
        val userId = UserId("user123")
        coEvery {
            userService.updateUser(
                id = userId,
                email = "updated.email@example.com"
            )
        }.answers {
            Result.success(
                User(
                    id = UserId("user123"),
                    email = "updated.email@example.com",
                    phoneNumber = "5051382468",
                    firstName = "Updated",
                    lastName = "Email",
                    authMetadata = null,
                    role = UserRole.USER
                )
            )
        }
        val contextRetriever = get<ContextRetriever>()
        val context = ClientContext.AuthenticatedClientContext(
            userInfo = mockk(),
            userId = UserId("user123"),
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRole(context, userId)
        }.answers {
            true
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
    fun `test updateUser fails when the user is trying to update another user`() = testEdifikanaApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_user_request.json")
        val expectedResponse = "You are not authorized to perform this action."
        val userService = get<UserService>()
        val rbacService = get<RBACService>()
        val userId = UserId("user654")
        val targetUserId = UserId("user123")
        val contextRetriever = get<ContextRetriever>()
        val context = ClientContext.AuthenticatedClientContext(
            userInfo = mockk(),
            userId = userId,
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRole(context, targetUserId)
        }.answers {
            false
        }

        // Act
        val response = client.put("user/user123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { userService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test deleteUser succeeds when user is self`() = testEdifikanaApplication {
        // Arrange
        val userService = get<UserService>()
        val rbacService = get<RBACService>()
        val userId = UserId("user123")
        coEvery {
            userService.deleteUser(userId)
        }.answers {
            Result.success(true)
        }
        val contextRetriever = get<ContextRetriever>()
        val context = ClientContext.AuthenticatedClientContext(
            userInfo = mockk(),
            userId = userId,
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRole(context, userId)
        }.answers {
            true
        }
        // Act
        val response = client.delete("user/user123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test deleteUser fails when user is trying to delete another user`() = testEdifikanaApplication {
        // Arrange
        val expectedResponse = "You are not authorized to perform this action."
        val userService = get<UserService>()
        val rbacService = get<RBACService>()
        val userId = UserId("user654")
        val targetUserId = UserId("user123")
        val contextRetriever = get<ContextRetriever>()
        val context = ClientContext.AuthenticatedClientContext(
            userInfo = mockk(),
            userId = userId,
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRole(context, targetUserId)
        }.answers {
            false
        }

        // Act
        val response = client.delete("user/user123")

        // Assert
        coVerify { userService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }
}
