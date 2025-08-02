package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.Routes
import com.cramsan.framework.ammotations.NetworkModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [EventLogServiceImpl].
 * TODO: SKELETON FOR TESTING, NEEDS TO BE UPDATED AS CLASS IS NOT VERY TESTABLE ATM
 */
@Ignore
class EventLogServiceImplTest {
    private val httpClient = mockk<HttpClient>()
    private val service = EventLogServiceImpl(httpClient)

    /**
     * Tests that getRecords returns a mapped list of event log records.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getRecords should return mapped records`() = runTest {
        // Arrange
        val networkResponse = listOf(
            mockk<EventLogEntryNetworkResponse> {
                coEvery { toEventLogRecordModel() } returns mockk<EventLogRecordModel>()
            },
            mockk<EventLogEntryNetworkResponse> {
                coEvery { toEventLogRecordModel() } returns mockk<EventLogRecordModel>()
            }
        )
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.get(Routes.EventLog.PATH) } returns mockk {
//            coEvery { body<List<EventLogEntryNetworkResponse>>() } returns networkResponse
        }

        // Act
        val result = service.getRecords()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    /**
     * Tests that getRecord returns a mapped record for the given eventLogRecordPK.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getRecord should return mapped record for eventLogRecordPK`() = runTest {
        // Arrange
        val eventLogEntryId = EventLogEntryId("event-1")
        val networkResponse = mockk<EventLogEntryNetworkResponse> {
            coEvery { toEventLogRecordModel() } returns mockk<EventLogRecordModel>()
        }
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.get("${Routes.EventLog.PATH}/${eventLogEntryId.eventLogEntryId}") } returns mockk {
//            coEvery { body<EventLogEntryNetworkResponse>() } returns networkResponse
        }

        // Act
        val result = service.getRecord(eventLogEntryId)

        // Assert
        assertTrue(result.isSuccess)
    }

    /**
     * Tests that addRecord returns a mapped record after creation.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `addRecord should return mapped record after creation`() = runTest {
        // Arrange
        val eventLogRecord = mockk<EventLogRecordModel> {
            coEvery { toCreateEventLogEntryNetworkRequest() } returns mockk()
        }
        val networkResponse = mockk<EventLogEntryNetworkResponse> {
            coEvery { toEventLogRecordModel() } returns mockk<EventLogRecordModel>()
        }
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.post(Routes.EventLog.PATH, any()) } returns mockk {
            coEvery { body<EventLogEntryNetworkResponse>() } returns networkResponse
        }

        // Act
        val result = service.addRecord(eventLogRecord)

        // Assert
        assertTrue(result.isSuccess)
    }

    /**
     * Tests that updateRecord returns a mapped record after update.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `updateRecord should return mapped record after update`() = runTest {
        // Arrange
        val eventLogEntryId = EventLogEntryId("event-2")
        val eventLogRecord = mockk<EventLogRecordModel> {
            coEvery { id } returns eventLogEntryId
            coEvery { toUpdateEventLogEntryNetworkRequest() } returns mockk()
        }
        val networkResponse = mockk<EventLogEntryNetworkResponse> {
            coEvery { toEventLogRecordModel() } returns mockk<EventLogRecordModel>()
        }
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.put("${Routes.EventLog.PATH}/${eventLogEntryId.eventLogEntryId}", any()) } returns mockk {
            coEvery { body<EventLogEntryNetworkResponse>() } returns networkResponse
        }

        // Act
        val result = service.updateRecord(eventLogRecord)

        // Assert
        assertTrue(result.isSuccess)
    }
}

