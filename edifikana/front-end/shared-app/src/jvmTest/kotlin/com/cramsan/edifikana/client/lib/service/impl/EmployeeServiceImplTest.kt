package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole

import com.cramsan.edifikana.lib.model.network.EmployeeNetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [EmployeeServiceImpl].
 * TODO: SKELETON FOR TESTING, NEEDS TO BE UPDATED AS CLASS IS NOT VERY TESTABLE ATM
 */
@Ignore
class EmployeeServiceImplTest {
    private val httpClient = mockk<HttpClient>()
    private val service = EmployeeServiceImpl(httpClient)

    /**
     * Tests that getEmployeeList returns a mapped list of employee models.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getEmployeeList should return mapped employee list`() = runTest {
        // Arrange
        val networkResponse = listOf(
            mockk<EmployeeNetworkResponse> {
                coEvery { toEmployeeModel() } returns EmployeeModel(
                    EmployeeId("employee-1"),
                    IdType.PASSPORT,
                    "John",
                    "Doe",
                    EmployeeRole.MANAGER,
                    "johndoe@email.com",
                    )
            },
            mockk<EmployeeNetworkResponse> {
                coEvery { toEmployeeModel() } returns EmployeeModel(
                    EmployeeId("employee-2"),
                    IdType.DNI,
                    "Jane",
                    "Smith",
                    EmployeeRole.SECURITY,
                    "janesmith@email.com",
                    )
            }
        )
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.get(any<String>()) } returns mockk {
//            coEvery { body<List<EmployeeNetworkResponse>>() } returns networkResponse
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
        val networkResponse = mockk<EmployeeNetworkResponse> {
            coEvery { toEmployeeModel() } returns EmployeeModel(
                EmployeeId("admin-1"),
                IdType.CE,
                "John",
                "Doe",
                EmployeeRole.MANAGER,
                "johnD@email.com",
            )
        }
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.get(any<String>()) } returns mockk {
//            coEvery { body<EmployeeNetworkResponse>() } returns networkResponse
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
        val createRequest = mockk<EmployeeModel.CreateEmployeeRequest> {
            coEvery { toCreateEmployeeNetworkRequest() } returns mockk()
        }
        val networkResponse = mockk<EmployeeNetworkResponse> {
            coEvery { toEmployeeModel() } returns EmployeeModel(
                EmployeeId("Admin-3"),
                IdType.DNI,
                "Alice",
                "Brown",
                EmployeeRole.MANAGER,
                "aliceBrn@email.com",
                )
        }
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.post(any<String>(), any()) } returns mockk {
            coEvery { body<EmployeeNetworkResponse>() } returns networkResponse
        }

        // Act
        val result = service.createEmployee(createRequest)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Alice", result.getOrNull()?.firstName)
    }
}

