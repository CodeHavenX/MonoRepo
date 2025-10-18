package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
 * Test class for [TimeCardServiceImpl].
 * TODO: SKELETON FOR TESTING, NEEDS TO BE UPDATED AS CLASS IS NOT VERY TESTABLE ATM
 */
@Ignore
class TimeCardServiceImplTest {
    private val httpClient = mockk<HttpClient>()
    private val service = TimeCardServiceImpl(httpClient)

    /**
     * Tests that getRecords returns mapped records for a specific employee member.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getRecords should return mapped records for employee`() = runTest {
        // Arrange
        val employeeId = EmployeeId("employee-1")
        val propertyId = PropertyId("Cenit")
        val networkResponse = listOf(
            mockk<TimeCardEventNetworkResponse> {
                coEvery { toTimeCardRecordModel() } returns TimeCardRecordModel(
                    TimeCardEventId("1"),
                    "clock-in",
                    employeeId,
                    PropertyId("Cenit"),
                    TimeCardEventType.CLOCK_IN,
                    System.currentTimeMillis(),
                    "",
                    "",
                )
            },
            mockk<TimeCardEventNetworkResponse> {
                coEvery { toTimeCardRecordModel() } returns TimeCardRecordModel(
                    TimeCardEventId("2"),
                    "clock-out",
                    employeeId,
                    PropertyId("Cenit"),
                    TimeCardEventType.CLOCK_OUT,
                    System.currentTimeMillis(),
                    "",
                    ""
                )
            }
        )
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.get(any<String>(), any()) } returns mockk {
            coEvery { body<List<TimeCardEventNetworkResponse>>() } returns networkResponse
        }

        // Act
        val result = service.getRecords(employeeId, propertyId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals(TimeCardEventType.CLOCK_IN, result.getOrNull()?.get(0)?.eventType)
        assertEquals(TimeCardEventType.CLOCK_OUT, result.getOrNull()?.get(1)?.eventType)
    }

    /**
     * Tests that getAllRecords returns mapped records for all employee members.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getAllRecords should return mapped records`() = runTest {
        // Arrange
        val propertyId = PropertyId("Cenit")
        val networkResponse = listOf(
            mockk<TimeCardEventNetworkResponse> {
                coEvery { toTimeCardRecordModel() } returns TimeCardRecordModel(
                    TimeCardEventId("1"),
                    "event1",
                    mockk(),
                    PropertyId("Cenit"),
                    TimeCardEventType.CLOCK_OUT,
                    System.currentTimeMillis(),
                    "",
                    "")
            }
        )
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.get(any<String>(), any()) } returns mockk {
            coEvery { body<List<TimeCardEventNetworkResponse>>() } returns networkResponse
        }

        // Act
        val result = service.getAllRecords(propertyId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals(TimeCardEventType.CLOCK_OUT, result.getOrNull()?.get(0)?.eventType)
    }

    /**
     * Tests that getRecord returns a mapped record for a specific eventId.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getRecord should return mapped record for eventId`() = runTest {
        // Arrange
        val eventId = TimeCardEventId("event-1")
        val networkResponse = mockk<TimeCardEventNetworkResponse> {
            coEvery { toTimeCardRecordModel() } returns TimeCardRecordModel(
                eventId,
                "event1",
                mockk(),
                PropertyId("Cenit"),
                TimeCardEventType.CLOCK_OUT,
                System.currentTimeMillis(),
                "",
                "")
        }
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.get(any<String>(), any()) } returns mockk {
            coEvery { body<TimeCardEventNetworkResponse>() } returns networkResponse
        }

        // Act
        val result = service.getRecord(eventId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(TimeCardEventType.CLOCK_OUT, result.getOrNull()?.eventType)
    }

    /**
     * Tests that addRecord returns success when a record is added.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `addRecord should return success when record is added`() = runTest {
        // Arrange
        val record = TimeCardRecordModel(
            TimeCardEventId("event-1"),
            "event1",
            mockk(),
            PropertyId("Cenit"),
            TimeCardEventType.CLOCK_OUT,
            System.currentTimeMillis(),
            "",
            "")
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.post(any<String>(), any()) } returns mockk {
            coEvery { body<Boolean>() } returns true
        }

        // Act
        val result = service.addRecord(record)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("event1", result.getOrNull()?.entityId)
    }
}
