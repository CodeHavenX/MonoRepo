package com.cramsan.edifikana.server.mcp

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.user.UserId
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
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PropertyMcpToolsTest :
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
    fun `test property_get_assigned_properties tool succeeds for an authenticated caller`() =
        testBackEndApplication(configFile = "application-test-mcp.conf") {
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
            val response = client.callMcpTool(name = "property_get_assigned_properties", arguments = "{}")

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            val result = Json.parseToJsonElement(response.bodyAsText()).jsonObject["result"]!!.jsonObject
            assertEquals(false, result["isError"]?.jsonPrimitive?.boolean)
            val text =
                result["content"]!!
                    .jsonArray[0]
                    .jsonObject["text"]!!
                    .jsonPrimitive.content
            assertEquals(expectedResponse, text)
        }

    @Test
    fun `test property_get_property tool succeeds when caller has required role`() =
        testBackEndApplication(configFile = "application-test-mcp.conf") {
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
            val context =
                ClientContext.AuthenticatedClientContext(
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
            val response =
                client.callMcpTool(
                    name = "property_get_property",
                    arguments = """{"propertyId":"property123"}""",
                )

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            val result = Json.parseToJsonElement(response.bodyAsText()).jsonObject["result"]!!.jsonObject
            assertEquals(false, result["isError"]?.jsonPrimitive?.boolean)
            val text =
                result["content"]!!
                    .jsonArray[0]
                    .jsonObject["text"]!!
                    .jsonPrimitive.content
            assertEquals(expectedResponse, text)
        }

    @Test
    fun `test property_get_property tool reports an MCP error when caller lacks required role`() =
        testBackEndApplication(configFile = "application-test-mcp.conf") {
            // Arrange
            val propertyService = get<PropertyService>()
            val rbacService = get<RBACService>()
            val propId = PropertyId("property123")
            val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
            val context =
                ClientContext.AuthenticatedClientContext(
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
            val response =
                client.callMcpTool(
                    name = "property_get_property",
                    arguments = """{"propertyId":"property123"}""",
                )

            // Assert: the MCP transport still responds 200 OK at the HTTP layer — the failure is reported
            // inside the JSON-RPC result via isError, per the MCP spec's tool-error-handling guidance.
            assertEquals(HttpStatusCode.OK, response.status)
            val result = Json.parseToJsonElement(response.bodyAsText()).jsonObject["result"]!!.jsonObject
            assertTrue(result["isError"]!!.jsonPrimitive.boolean)
            val text =
                result["content"]!!
                    .jsonArray[0]
                    .jsonObject["text"]!!
                    .jsonPrimitive.content
            assertTrue(text.contains("You are not authorized to perform this action in your organization."))
            coVerify { propertyService wasNot Called }
        }

    private suspend fun HttpClient.callMcpTool(name: String, arguments: String) =
        post("mcp") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Accept, "application/json, text/event-stream")
            header(HttpHeaders.Host, "localhost")
            setBody(
                """
                {
                    "jsonrpc": "2.0",
                    "id": 1,
                    "method": "tools/call",
                    "params": {
                        "name": "$name",
                        "arguments": $arguments
                    }
                }
                """.trimIndent(),
            )
        }
}
