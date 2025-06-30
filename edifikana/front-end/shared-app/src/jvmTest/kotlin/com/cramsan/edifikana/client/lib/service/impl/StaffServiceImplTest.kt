package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.StaffStatus
import com.cramsan.edifikana.lib.model.network.StaffNetworkResponse
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
 * Test class for [StaffServiceImpl].
 * TODO: SKELETON FOR TESTING, NEEDS TO BE UPDATED AS CLASS IS NOT VERY TESTABLE ATM
 */
@Ignore
class StaffServiceImplTest {
    private val httpClient = mockk<HttpClient>()
    private val service = StaffServiceImpl(httpClient)

    /**
     * Tests that getStaffList returns a mapped list of staff models.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getStaffList should return mapped staff list`() = runTest {
        // Arrange
        val networkResponse = listOf(
            mockk<StaffNetworkResponse> {
                coEvery { toStaffModel() } returns StaffModel(
                    StaffId("employee-1"),
                    IdType.PASSPORT,
                    "John",
                    "Doe",
                    StaffRole.ADMIN,
                    "johndoe@email.com",
                    StaffStatus.ACTIVE,
                    )
            },
            mockk<StaffNetworkResponse> {
                coEvery { toStaffModel() } returns StaffModel(
                    StaffId("employee-2"),
                    IdType.DNI,
                    "Jane",
                    "Smith",
                    StaffRole.SECURITY,
                    "janesmith@email.com",
                    StaffStatus.PENDING,
                    )
            }
        )
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.get(any<String>()) } returns mockk {
//            coEvery { body<List<StaffNetworkResponse>>() } returns networkResponse
        }

        // Act
        val result = service.getStaffList()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("John", result.getOrNull()?.get(0)?.firstName)
        assertEquals("Jane", result.getOrNull()?.get(1)?.firstName)
    }

    /**
     * Tests that getStaff returns a mapped staff model for the given staffPK.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getStaff should return mapped staff for staffPK`() = runTest {
        // Arrange
        val staffId = StaffId("1")
        val networkResponse = mockk<StaffNetworkResponse> {
            coEvery { toStaffModel() } returns StaffModel(
                StaffId("admin-1"),
                IdType.CE,
                "John",
                "Doe",
                StaffRole.ADMIN,
                "johnD@email.com",
                StaffStatus.ACTIVE,
            )
        }
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.get(any<String>()) } returns mockk {
//            coEvery { body<StaffNetworkResponse>() } returns networkResponse
        }

        // Act
        val result = service.getStaff(staffId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("John", result.getOrNull()?.firstName)
    }

    /**
     * Tests that createStaff returns a mapped staff model after creation.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `createStaff should return mapped staff after creation`() = runTest {
        // Arrange
        val createRequest = mockk<StaffModel.CreateStaffRequest> {
            coEvery { toCreateStaffNetworkRequest() } returns mockk()
        }
        val networkResponse = mockk<StaffNetworkResponse> {
            coEvery { toStaffModel() } returns StaffModel(
                StaffId("Admin-3"),
                IdType.DNI,
                "Alice",
                "Brown",
                StaffRole.ADMIN,
                "aliceBrn@email.com",
                StaffStatus.ACTIVE,
                )
        }
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.post(any<String>(), any()) } returns mockk {
            coEvery { body<StaffNetworkResponse>() } returns networkResponse
        }

        // Act
        val result = service.createStaff(createRequest)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Alice", result.getOrNull()?.firstName)
    }
}

