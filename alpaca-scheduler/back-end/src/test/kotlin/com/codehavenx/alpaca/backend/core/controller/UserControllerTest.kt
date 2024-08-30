package com.codehavenx.alpaca.backend.core.controller

import com.codehavenx.alpaca.backend.core.service.UserService
import com.codehavenx.alpaca.backend.core.service.models.User
import com.codehavenx.alpaca.backend.core.service.models.UserId
import com.codehavenx.alpaca.backend.utils.readFileContent
import com.cramsan.framework.test.TestBase
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
import kotlin.test.Test
import kotlin.test.assertEquals

class UserControllerTest : TestBase(), KoinTest {

    override fun setupTest() = Unit

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test createUser`() = testAlpacaApplication {
        // Configure
        startTestKoin()
        val requestBody = readFileContent("requests/user_create_request.json")
        val responseBody = readFileContent("requests/user_create_response.json")
        val userService = get<UserService>()
        coEvery { userService.createUser("test", "1234567890", "example@test.com") }
            .answers {
                User(
                    id = UserId("123"),
                    isVerified = false,
                    username = "test",
                    phoneNumber = emptyList(),
                    firstName = null,
                    lastName = null,
                    address = null,
                    email = emptyList(),
                )
            }

        // Act
        val response = client.post("user") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(responseBody, response.bodyAsText())
    }
}
