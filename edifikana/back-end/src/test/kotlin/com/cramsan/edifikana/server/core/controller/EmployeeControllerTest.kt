package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.StaffService
import com.cramsan.edifikana.server.core.service.models.Staff
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

class EmployeeControllerTest : CoroutineTest(), KoinTest {

    @BeforeTest
    fun setupTest() {
        startTestKoin()
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test createStaff`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/create_staff_request.json")
        val expectedResponse = readFileContent("requests/create_staff_response.json")
        val staffService = get<StaffService>()
        coEvery {
            staffService.createStaff(
                IdType.DNI,
                "John",
                "Doe",
                StaffRole.SECURITY,
                PropertyId("property123"),
            )
        }.answers {
            Staff(
                id = StaffId("staff123"),
                firstName = "John",
                lastName = "Doe",
                idType = IdType.DNI,
                role = StaffRole.SECURITY,
                propertyId = PropertyId("property123"),
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
        val response = client.post("staff") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getStaff`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_staff_response.json")
        val staffService = get<StaffService>()
        coEvery {
            staffService.getStaff(StaffId("staff123"))
        }.answers {
            Staff(
                id = StaffId("staff123"),
                firstName = "John",
                lastName = "Doe",
                idType = IdType.DNI,
                role = StaffRole.SECURITY,
                propertyId = PropertyId("property123"),
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
        val response = client.get("staff/staff123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getStaffs`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_staffs_response.json")
        val staffService = get<StaffService>()
        val contextRetriever = get<ContextRetriever>()
        val clientContext = ClientContext.AuthenticatedClientContext(
            userInfo = mockk(),
            userId = UserId("user123"),
        )

        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            clientContext
        }
        coEvery {
            staffService.getStaffs(clientContext)
        }.answers {
            listOf(
                Staff(
                    id = StaffId("staff123"),
                    firstName = "John",
                    lastName = "Doe",
                    idType = IdType.DNI,
                    role = StaffRole.SECURITY,
                    propertyId = PropertyId("property123"),
                ),
                Staff(
                    id = StaffId("staff456"),
                    firstName = "Jane",
                    lastName = "Smith",
                    idType = IdType.PASSPORT,
                    role = StaffRole.CLEANING,
                    propertyId = PropertyId("property456"),
                )
            )
        }

        // Act
        val response = client.get("staff")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateStaff`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/update_staff_request.json")
        val expectedResponse = readFileContent("requests/update_staff_response.json")
        val staffService = get<StaffService>()
        coEvery {
            staffService.updateStaff(
                id = StaffId("staff123"),
                idType = IdType.CE,
                firstName = "Cesar",
                lastName = "Ramirez",
                role = StaffRole.SECURITY_COVER,
            )
        }.answers {
            Staff(
                id = StaffId("staff123"),
                firstName = "Cesar",
                lastName = "Ramirez",
                idType = IdType.CE,
                role = StaffRole.SECURITY_COVER,
                propertyId = PropertyId("property456"),
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
        val response = client.put("staff/staff123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test deleteStaff`() = testEdifikanaApplication {
        // Configure
        val staffService = get<StaffService>()
        coEvery {
            staffService.deleteStaff(StaffId("staff123"))
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
        val response = client.delete("staff/staff123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
