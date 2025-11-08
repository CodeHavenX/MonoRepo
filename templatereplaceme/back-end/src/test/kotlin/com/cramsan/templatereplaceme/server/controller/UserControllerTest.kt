package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.test.CoroutineTest
import com.cramsan.templatereplaceme.lib.model.UserId
import com.cramsan.templatereplaceme.server.readFileContent
import com.cramsan.templatereplaceme.server.service.UserService
import com.cramsan.templatereplaceme.server.service.models.User
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
    fun `test createUser`() = testTemplateReplaceMeApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_user_request.json")
        val expectedResponse = readFileContent("requests/create_user_response.json")
        val userService = get<UserService>()
        coEvery {
            userService.createUser(
                firstName = "John",
                lastName = "Doe",
            )
        }.answers {
            Result.success(
                User(
                    id = UserId("user123"),
                    firstName = "John",
                    lastName = "Doe",
                )
            )
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.UnauthenticatedClientContext
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
}
