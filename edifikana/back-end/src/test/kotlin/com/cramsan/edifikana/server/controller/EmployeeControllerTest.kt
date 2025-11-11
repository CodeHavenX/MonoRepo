package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.EmployeeService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.Employee
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

class EmployeeControllerTest : CoroutineTest(), KoinTest {

    @BeforeTest
    fun setupTest() {
        startTestKoin(
            testApplicationModule(createJson()),
            TestControllerModule,
            TestServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test createEmployee succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_employee_request.json")
        val expectedResponse = readFileContent("requests/create_employee_response.json")
        val employeeService = get<EmployeeService>()
        val rbacService = get<RBACService>()
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
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext<SupabaseContextPayload>(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, PropertyId("property123"), UserRole.ADMIN) } returns true

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
    fun `test createEmployee fails when user doesn't have required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_employee_request.json")
        val employeeService = get<EmployeeService>()
        val rbacService = get<RBACService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, PropertyId("property123"), UserRole.ADMIN) } returns false

        // Act
        val response = client.post("employee") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { employeeService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getEmployee succeeds when user has required role or higher`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_employee_response.json")
        val employeeService = get<EmployeeService>()
        val rbacService = get<RBACService>()
        val empId = EmployeeId("emp123")
        coEvery {
            employeeService.getEmployee(empId)
        } returns Employee(
            id = empId,
            firstName = "John",
            lastName = "Doe",
            idType = IdType.DNI,
            role = EmployeeRole.SECURITY,
            propertyId = PropertyId("property123"),
        )
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, empId, UserRole.MANAGER) } returns true

        // Act
        val response = client.get("employee/emp123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getEmployee fails when user doesn't have required role or higher`() = testBackEndApplication {
        // Arrange
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val employeeService = get<EmployeeService>()
        val rbacService = get<RBACService>()
        val empId = EmployeeId("emp123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, empId, UserRole.MANAGER) } returns false

        // Act
        val response = client.get("employee/emp123")

        // Assert
        coVerify { employeeService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getEmployees succeeds when user has required role or higher`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_employees_response.json")
        val employeeService = get<EmployeeService>()
        val rbacService = get<RBACService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val clientContext = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
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
    fun `test updateEmployee succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_employee_request.json")
        val expectedResponse = readFileContent("requests/update_employee_response.json")
        val employeeService = get<EmployeeService>()
        val rbacService = get<RBACService>()
        val empId = EmployeeId("emp123")
        coEvery {
            employeeService.updateEmployee(
                id = empId,
                idType = IdType.CE,
                firstName = "Cesar",
                lastName = "Ramirez",
                role = EmployeeRole.SECURITY_COVER,
            )
        } returns Employee(
            id = empId,
            firstName = "Cesar",
            lastName = "Ramirez",
            idType = IdType.CE,
            role = EmployeeRole.SECURITY_COVER,
            propertyId = PropertyId("property456"),
        )
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, empId, UserRole.ADMIN) } returns true

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
    fun `test updateEmployee fails when user doesn't have required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_employee_request.json")
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val employeeService = get<EmployeeService>()
        val rbacService = get<RBACService>()
        val empId = EmployeeId("emp123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, empId, UserRole.ADMIN) } returns false

        // Act
        val response = client.put("employee/emp123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { employeeService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test deleteEmployee succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val employeeService = get<EmployeeService>()
        val rbacService = get<RBACService>()
        val empId = EmployeeId("emp123")
        coEvery { employeeService.deleteEmployee(empId) } returns true
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, empId, UserRole.ADMIN) } returns true

        // Act
        val response = client.delete("employee/emp123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test deleteEmployee fails when user doesn't have required role`() = testBackEndApplication {
        // Arrange
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val employeeService = get<EmployeeService>()
        val rbacService = get<RBACService>()
        val empId = EmployeeId("emp123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, empId, UserRole.ADMIN) } returns false

        // Act
        val response = client.delete("employee/emp123")

        // Assert
        coVerify { employeeService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }
}
