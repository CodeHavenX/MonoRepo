package com.codehavenx.alpaca.backend.core.controller

import com.codehavenx.alpaca.backend.core.service.UserService
import com.codehavenx.alpaca.backend.core.service.models.User
import com.codehavenx.alpaca.backend.core.service.models.UserId
import com.codehavenx.alpaca.backend.utils.readFileContent
import com.cramsan.framework.test.TestBase
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
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
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test the [UserController] class.
 */
class UserControllerTest : TestBase(), KoinTest {

    /**
     * Setup the test.
     */
    override fun setupTest() = Unit

    /**
     * Clean up the test.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Test the [UserController.createUser] function. Verifies the response is formatted as expected.
     */
    @Test
    fun `test createUser`() = testAlpacaApplication {
        // Configure
        startTestKoin()
        val requestBody = readFileContent("requests/user_create_request.json")
        val expectedResponse = readFileContent("requests/user_create_response.json")
        val userService = get<UserService>()
        coEvery { userService.createUser("test", "1234567890", "example@test.com") }
            .answers {
                User(
                    id = UserId("123"),
                    isVerified = false,
                    username = "test",
                    phoneNumbers = listOf("1234567890"),
                    firstName = null,
                    lastName = null,
                    address = null,
                    emails = listOf("example@test.com"),
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
     * Test the [UserController.getUser] function.
     */
    @Test
    fun `test get single user with ID`() = testAlpacaApplication {
        // Configure
        startTestKoin()
        val expectedResponse = readFileContent("requests/get_single_user_response.json")
        val userService = get<UserService>()
        coEvery { userService.getUser(UserId("AbdeD87412")) }
            .answers {
                User(
                    id = UserId("AbdeD87412"),
                    isVerified = true,
                    username = "myUser",
                    phoneNumbers = emptyList(),
                    firstName = null,
                    lastName = "Delanore",
                    address = null,
                    emails = emptyList(),
                )
            }

        // Act
        val response = client.get("user/AbdeD87412")

        // Assert
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    /**
     * Test the [UserController.updateUser] function.
     * This test will update a users existing username and phone number to a new value, while updating the address and
     * emails to null
     */
    @Test
    fun `test update a single user`() = testAlpacaApplication {
        // Configure
        startTestKoin()
        val requestBody = readFileContent("requests/update_single_user_request.json")
        val expectedResponse = readFileContent("requests/update_single_user_response.json")
        val userService = get<UserService>()
        coEvery { userService.updateUser(UserId("4387DERE"), "UpdateMe") }
            .answers {
                User(
                    id = UserId("4387DERE"),
                    isVerified = false,
                    username = "UpdateMe",
                    phoneNumbers = listOf("1234567890"),
                    firstName = null,
                    lastName = null,
                    address = null,
                    emails = emptyList()
                )
            }

        // Act
        val response = client.put("user/4387DERE") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }
}
