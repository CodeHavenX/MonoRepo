package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.EventLogEntryListNetworkResponse
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
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
 * Test class for [EventLogServiceImpl].
 */
class EventLogServiceImplTest {
    private lateinit var ktorTestEngine: KtorTestEngine
    private lateinit var httpClient: HttpClient
    private lateinit var service: EventLogServiceImpl
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
        service = EventLogServiceImpl(httpClient)

        AssertUtil.setInstance(NoopAssertUtil())
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    /**
     * Tests that getRecord returns a mapped record for the given eventLogRecordPK.
     */
    @Test
    @OptIn(NetworkModel::class)
    fun `getRecord should return mapped record for eventLogRecordPK`() = runTest {
        // Arrange
        val eventLogEntryId = EventLogEntryId("event-1")
        val networkResponse = EventLogEntryNetworkResponse(
            id = eventLogEntryId,
            employeeId = EmployeeId("employee-1"),
            fallbackEmployeeName = "John Doe",
            propertyId = PropertyId("property-1"),
            type = EventLogEventType.MAINTENANCE_SERVICE,
            description = "Event 1 description",
            fallbackEventType = "Maintenance Service",
            timestamp = 2342341234L,
            title = "Event 1",
            unit = "Unit 1",
        )
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getRecord(eventLogEntryId)

        // Assert
        assertTrue(result.isSuccess)
    }

    /**
     * Tests that getRecords returns a mapped list of event log records.
     */
    @Test
    @OptIn(NetworkModel::class)
    fun `getRecords should return mapped records`() = runTest {
        // Arrange
        val propertyId = PropertyId("Muralla")
        val networkResponse = EventLogEntryListNetworkResponse(listOf(
            EventLogEntryNetworkResponse(
                id = EventLogEntryId("event-1"),
                employeeId = EmployeeId("employee-1"),
                fallbackEmployeeName = "John Doe",
                propertyId = PropertyId("property-1"),
                type = EventLogEventType.MAINTENANCE_SERVICE,
                description = "Event 1 description",
                fallbackEventType = "Maintenance Service",
                timestamp = 2342341234L,
                title = "Event 1",
                unit = "Unit 1",
            ),
        ))
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getRecords(propertyId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    /**
     * Tests that addRecord returns a mapped record after creation.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `addRecord should return mapped record after creation`() = runTest {
        // Arrange
        val eventLogRecord = EventLogRecordModel(
            id = null,
            employeePk = EmployeeId("employee-1"),
            propertyId = PropertyId("property-1"),
            eventType = EventLogEventType.MAINTENANCE_SERVICE,
            description = "Event 1 description",
            timeRecorded = 2342341234L,
            title = "Event 1",
            fallbackEmployeeName = "John Doe",
            fallbackEventType = "Maintenance Service",
            attachments = emptyList(),
            entityId = null,
            unit = "Unit 1",
        )
        val networkResponse = EventLogEntryNetworkResponse(
            id = EventLogEntryId("event-1"),
            employeeId = EmployeeId("employee-1"),
            fallbackEmployeeName = "John Doe",
            propertyId = PropertyId("property-1"),
            type = EventLogEventType.MAINTENANCE_SERVICE,
            description = "Event 1 description",
            fallbackEventType = "Maintenance Service",
            timestamp = 2342341234L,
            title = "Event 1",
            unit = "Unit 1",
        )
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
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
        val eventLogRecord = EventLogRecordModel(
            id = EventLogEntryId("event-1"),
            employeePk = EmployeeId("employee-1"),
            propertyId = PropertyId("property-1"),
            eventType = EventLogEventType.MAINTENANCE_SERVICE,
            description = "Updated Event 1 description",
            timeRecorded = 2342341234L,
            title = "Updated Event 1",
            fallbackEmployeeName = "John Doe",
            fallbackEventType = "Maintenance Service",
            attachments = emptyList(),
            entityId = null,
            unit = "Unit 1",
        )
        val networkResponse = EventLogEntryNetworkResponse(
            id = EventLogEntryId("event-1"),
            employeeId = EmployeeId("employee-1"),
            fallbackEmployeeName = "John Doe",
            propertyId = PropertyId("property-1"),
            type = EventLogEventType.MAINTENANCE_SERVICE,
            description = "Updated Event 1 description",
            fallbackEventType = "Maintenance Service",
            timestamp = 2342341234L,
            title = "Updated Event 1",
            unit = "Unit 1",
        )
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.updateRecord(eventLogRecord)

        // Assert
        assertTrue(result.isSuccess)
    }
}

