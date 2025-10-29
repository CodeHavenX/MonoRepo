package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.EmployeeListNetworkResponse
import com.cramsan.edifikana.lib.model.network.EmployeeNetworkResponse
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [EmployeeServiceImpl].
 */
class EmployeeServiceImplTest {
    private lateinit var ktorTestEngine: KtorTestEngine
    private lateinit var httpClient: HttpClient
    private lateinit var service: EmployeeServiceImpl
    private lateinit var json: Json

    /**
     * Setup the test environment.
     */
    @BeforeTest
    fun setupTest() {
        ktorTestEngine = KtorTestEngine()
        json = createJson()
        httpClient = HttpClient(ktorTestEngine.engine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
        service = EmployeeServiceImpl(httpClient)

        AssertUtil.setInstance(NoopAssertUtil())
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    /**
     * Tests that getEmployeeList returns a mapped list of employee models.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getEmployeeList should return mapped employee list`() = runTest {
        // Arrange
        val networkResponse = EmployeeListNetworkResponse(listOf(
            EmployeeNetworkResponse(
                EmployeeId("admin-1"),
                IdType.CE,
                "John",
                "Doe",
                EmployeeRole.MANAGER,
                propertyId = PropertyId("property-1"),
            ),
            EmployeeNetworkResponse(
                EmployeeId("admin-2"),
                IdType.DNI,
                "Jane",
                "Smith",
                EmployeeRole.SECURITY,
                propertyId = PropertyId("property-1"),
            ),
        ))
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getEmployeeList()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("John", result.getOrNull()?.get(0)?.firstName)
        assertEquals("Jane", result.getOrNull()?.get(1)?.firstName)
    }

    /**
     * Tests that getEmployee returns a mapped employee model for the given employeePK.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getEmployee should return mapped employee for employeePK`() = runTest {
        // Arrange
        val employeeId = EmployeeId("1")
        val networkResponse = EmployeeNetworkResponse(
            EmployeeId("admin-1"),
            IdType.CE,
            "John",
            "Doe",
            EmployeeRole.MANAGER,
            propertyId = PropertyId("property-1"),
        )
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getEmployee(employeeId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("John", result.getOrNull()?.firstName)
    }

    /**
     * Tests that createEmployee returns a mapped employee model after creation.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `createEmployee should return mapped employee after creation`() = runTest {
        // Arrange
        val createRequest = EmployeeModel.CreateEmployeeRequest(
            IdType.DNI,
            "Alice",
            "Brown",
            EmployeeRole.MANAGER,
            PropertyId("property-2")
        )
        val networkResponse = EmployeeNetworkResponse(
            EmployeeId("admin-3"),
            IdType.DNI,
            "Alice",
            "Brown",
            EmployeeRole.MANAGER,
            propertyId = PropertyId("property-2"),
        )

        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.createEmployee(createRequest)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Alice", result.getOrNull()?.firstName)
    }

    /**
     * Tests that updateEmployee returns a mapped employee model after update.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `updateEmployee should return mapped employee after update`() = runTest {
        // Arrange
        val updateRequest = EmployeeModel.UpdateEmployeeRequest(
            EmployeeId("admin-1"),
            "John",
            "DoeUpdated",
            EmployeeRole.MANAGER,
        )
        val networkResponse = EmployeeNetworkResponse(
            EmployeeId("admin-1"),
            IdType.CE,
            "John",
            "DoeUpdated",
            EmployeeRole.MANAGER,
            propertyId = PropertyId("property-1"),
        )
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.updateEmployee(updateRequest)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("DoeUpdated", result.getOrNull()?.lastName)
    }
}

