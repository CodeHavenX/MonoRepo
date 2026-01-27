package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.PropertyService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.Property
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
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

class PropertyControllerTest :
    CoroutineTest(),
    KoinTest {

    /**
     * Setup the test.
     */
    @BeforeTest
    fun setupTest() {
        startTestKoin(
            testApplicationModule(createJson()),
            TestControllerModule,
            TestServiceModule,
        )
    }

    /**
     * Clean up the test.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test createProperty succeeds when user has required role in organization`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_property_request.json")
        val expectedResponse = readFileContent("requests/create_property_response.json")
        val propertyService = get<PropertyService>()
        val rbacService = get<RBACService>()
        coEvery {
            propertyService.createProperty(
                "building 1",
                "1234, Nairobi",
                OrganizationId("org123"),
                "drawable:CASA",
                any(),
            )
        }.answers {
            Property(
                id = PropertyId("property123"),
                name = "building 1",
                address = "1234, Nairobi",
                organizationId = OrganizationId("org123"),
                imageUrl = "drawable:CASA",
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            ),
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, OrganizationId("org123"), UserRole.ADMIN)
        }.answers {
            true
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
    fun `test createProperty fails when the user doesn't have the required role in their org`() =
        testBackEndApplication {
            // Arrange
            val requestBody = readFileContent("requests/create_property_request.json")
            val propertyService = get<PropertyService>()
            val rbacService = get<RBACService>()
            val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
            val expectedResponse = "You are not authorized to perform this action in your organization."
            val context = ClientContext.AuthenticatedClientContext(
                SupabaseContextPayload(
                    userInfo = mockk(),
                    userId = UserId("user123"),
                ),
            )
            coEvery {
                contextRetriever.getContext(any())
            }.answers {
                context
            }
            coEvery {
                rbacService.hasRoleOrHigher(context, OrganizationId("org123"), UserRole.ADMIN)
            }.answers {
                false
            }

            // Act
            val response = client.post("property") {
                setBody(requestBody)
                contentType(ContentType.Application.Json)
            }

            // Assert
            coVerify { propertyService wasNot Called }
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            assertEquals(expectedResponse, response.bodyAsText())
        }

    @Test
    fun `test getProperty succeeds when use has required role or higher`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_property_response.json")
        val propertyService = get<PropertyService>()
        val rbacService = get<RBACService>()
        val propId = PropertyId("property123")
        coEvery {
            propertyService.getProperty(propId)
        }.answers {
            Property(
                id = PropertyId("property123"),
                name = "building 1",
                address = "123 Main St",
                organizationId = OrganizationId("org123"),
                imageUrl = "drawable:CASA",
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            ),
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, propId, UserRole.MANAGER)
        }.answers {
            true
        }

        // Act
        val response = client.get("property/property123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getProperty fails when user doesn't have required role or higher`() = testBackEndApplication {
        // Arrange
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val propertyService = get<PropertyService>()
        val rbacService = get<RBACService>()
        val propId = PropertyId("property123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            ),
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, propId, UserRole.MANAGER)
        }.answers {
            false
        }

        // Act
        val response = client.get("property/property123")

        // Assert
        coVerify { propertyService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    // TODO: Update this test and add a negative check test for ensuring user get only list of properties they're assigned
    @Test
    fun `test getProperties`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_properties_response.json")
        val propertyService = get<PropertyService>()
        coEvery {
            propertyService.getProperties(UserId("user123"))
        }.answers {
            listOf(
                Property(
                    id = PropertyId("property123"),
                    name = "building 1",
                    address = "123 Main St",
                    organizationId = OrganizationId("org123"),
                    imageUrl = "drawable:CASA",
                ),
                Property(
                    id = PropertyId("property456"),
                    name = "building 2",
                    address = "456 Elm St",
                    organizationId = OrganizationId("org123"),
                    imageUrl = "drawable:S_DEPA",
                ),
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                SupabaseContextPayload(
                    userInfo = mockk(),
                    userId = UserId("user123"),
                ),
            )
        }

        // Act
        val response = client.get("property")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateProperty succeeds when the user has required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_property_request.json")
        val expectedResponse = readFileContent("requests/update_property_response.json")
        val propertyService = get<PropertyService>()
        val rbacService = get<RBACService>()
        val propId = PropertyId("property123")
        coEvery {
            propertyService.updateProperty(
                id = propId,
                name = "Updated Property",
                address = "calle bottger",
                imageUrl = "drawable:QUINTA",
            )
        }.answers {
            Property(
                id = PropertyId("property123"),
                name = "Updated Property",
                address = "calle bottger",
                organizationId = OrganizationId("org123"),
                imageUrl = "drawable:QUINTA",
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            ),
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, propId, UserRole.ADMIN)
        }.answers {
            true
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
    fun `test updateProperty fails when the user doesn't have required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_property_request.json")
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val propertyService = get<PropertyService>()
        val rbacService = get<RBACService>()
        val propId = PropertyId("property123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            ),
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, propId, UserRole.ADMIN)
        }.answers {
            false
        }

        // Act
        val response = client.put("property/property123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { propertyService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test deleteProperty succeeds when user has require role`() = testBackEndApplication {
        // Arrange
        val propertyService = get<PropertyService>()
        val rbacService = get<RBACService>()
        val propId = PropertyId("property123")
        coEvery {
            propertyService.deleteProperty(propId)
        }.answers {
            true
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            ),
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, propId, UserRole.ADMIN)
        }.answers {
            true
        }

        // Act
        val response = client.delete("property/property123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test deleteProperty fails when user doesn't have required role`() = testBackEndApplication {
        // Arrange
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val propertyService = get<PropertyService>()
        val rbacService = get<RBACService>()
        val propId = PropertyId("property123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            ),
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, propId, UserRole.ADMIN)
        }.answers {
            false
        }

        // Act
        val response = client.delete("property/property123")

        // Assert
        coVerify { propertyService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }
}
