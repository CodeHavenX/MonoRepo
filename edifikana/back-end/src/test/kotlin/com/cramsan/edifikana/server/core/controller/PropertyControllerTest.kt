package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.auth.ClientContext
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.PropertyService
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.test.CoroutineTest
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

class PropertyControllerTest : CoroutineTest(), KoinTest {

    /**
     * Setup the test.
     */
    @BeforeTest
    fun setupTest() {
        startTestKoin()
    }

    /**
     * Clean up the test.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test createProperty`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/create_property_request.json")
        val expectedResponse = readFileContent("requests/create_property_response.json")
        val propertyService = get<PropertyService>()
        coEvery {
            propertyService.createProperty("building 1")
        }.answers {
            Property(
                id = PropertyId("property123"),
                name = "building 1"
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
        val response = client.post("property") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getProperty`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_property_response.json")
        val propertyService = get<PropertyService>()
        coEvery {
            propertyService.getProperty(PropertyId("property123"))
        }.answers {
            Property(
                id = PropertyId("property123"),
                name = "building 1"
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
        val response = client.get("property/property123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getProperties`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_properties_response.json")
        val propertyService = get<PropertyService>()
        coEvery {
            propertyService.getProperties(UserId("user123"))
        }.answers {
            listOf(
                Property(
                    id = PropertyId("property123"),
                    name = "building 1"
                ),
                Property(
                    id = PropertyId("property456"),
                    name = "building 2"
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
        val response = client.get("property")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateProperty`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/update_property_request.json")
        val expectedResponse = readFileContent("requests/update_property_response.json")
        val propertyService = get<PropertyService>()
        coEvery {
            propertyService.updateProperty(
                id = PropertyId("property123"),
                name = "Updated Property"
            )
        }.answers {
            Property(
                id = PropertyId("property123"),
                name = "Updated Property"
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
        val response = client.put("property/property123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test deleteProperty`() = testEdifikanaApplication {
        // Configure
        val propertyService = get<PropertyService>()
        coEvery {
            propertyService.deleteProperty(PropertyId("property123"))
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
        val response = client.delete("property/property123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
