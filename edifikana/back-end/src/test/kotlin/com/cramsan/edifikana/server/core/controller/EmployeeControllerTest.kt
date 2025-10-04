package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.EmployeeService
import com.cramsan.edifikana.server.core.service.models.Employee
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
    fun `test createEmployee`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/create_employee_request.json")
        val expectedResponse = readFileContent("requests/create_employee_response.json")
        val employeeService = get<EmployeeService>()
        coEvery {
            employeeService.createEmployee(
                IdType.DNI,
                "John",
                "Doe",
                EmployeeRole.SECURITY,
                PropertyId("property123"),
            )
        }.answers {
            Employee(
                id = EmployeeId("emp123"),
                firstName = "John",
                lastName = "Doe",
                idType = IdType.DNI,
                role = EmployeeRole.SECURITY,
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
        val response = client.post("employee") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getEmployee`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_employee_response.json")
        val employeeService = get<EmployeeService>()
        coEvery {
            employeeService.getEmployee(EmployeeId("emp123"))
        }.answers {
            Employee(
                id = EmployeeId("emp123"),
                firstName = "John",
                lastName = "Doe",
                idType = IdType.DNI,
                role = EmployeeRole.SECURITY,
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
        val response = client.get("employee/emp123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getEmployees`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_employees_response.json")
        val employeeService = get<EmployeeService>()
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
            employeeService.getEmployees(clientContext)
        }.answers {
            listOf(
                Employee(
                    id = EmployeeId("emp123"),
                    firstName = "John",
                    lastName = "Doe",
                    idType = IdType.DNI,
                    role = EmployeeRole.SECURITY,
                    propertyId = PropertyId("property123"),
                ),
                Employee(
                    id = EmployeeId("emp456"),
                    firstName = "Jane",
                    lastName = "Smith",
                    idType = IdType.PASSPORT,
                    role = EmployeeRole.CLEANING,
                    propertyId = PropertyId("property456"),
                )
            )
        }

        // Act
        val response = client.get("employee")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateEmployee`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/update_employee_request.json")
        val expectedResponse = readFileContent("requests/update_employee_response.json")
        val employeeService = get<EmployeeService>()
        coEvery {
            employeeService.updateEmployee(
                id = EmployeeId("emp123"),
                idType = IdType.CE,
                firstName = "Cesar",
                lastName = "Ramirez",
                role = EmployeeRole.SECURITY_COVER,
            )
        }.answers {
            Employee(
                id = EmployeeId("emp123"),
                firstName = "Cesar",
                lastName = "Ramirez",
                idType = IdType.CE,
                role = EmployeeRole.SECURITY_COVER,
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
        val response = client.put("employee/emp123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test deleteEmployee`() = testEdifikanaApplication {
        // Configure
        val employeeService = get<EmployeeService>()
        coEvery {
            employeeService.deleteEmployee(EmployeeId("emp123"))
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
        val response = client.delete("employee/emp123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
