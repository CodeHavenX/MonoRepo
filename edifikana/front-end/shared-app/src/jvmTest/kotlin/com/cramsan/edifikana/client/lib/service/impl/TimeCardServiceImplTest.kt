package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.network.TimeCardEventListNetworkResponse
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
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

class TimeCardServiceImplTest {
    private lateinit var ktorTestEngine: KtorTestEngine
    private lateinit var httpClient: HttpClient
    private lateinit var service: TimeCardServiceImpl
    private lateinit var json: Json

    @BeforeTest
    fun setupTest() {
        ktorTestEngine = KtorTestEngine()
        json = createJson()
        httpClient = HttpClient(ktorTestEngine.engine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
        service = TimeCardServiceImpl(httpClient)

        AssertUtil.setInstance(NoopAssertUtil())
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `getRecords should return mapped time card records for employee`() = runTest {
        // Arrange
        val networkResponse = TimeCardEventListNetworkResponse(listOf(
            TimeCardEventNetworkResponse(
                id = TimeCardEventId("tc-1"),
                employeeId = EmployeeId("employee-1"),
                propertyId = PropertyId("property-1"),
                type = TimeCardEventType.CLOCK_IN,
                imageUrl = "http://example.com/image1.jpg",
                fallbackEmployeeName = "John Doe",
                timestamp = 311324800L,
            ),
            TimeCardEventNetworkResponse(
                id = TimeCardEventId("tc-2"),
                employeeId = EmployeeId("employee-2"),
                propertyId = PropertyId("property-1"),
                type = TimeCardEventType.CLOCK_OUT,
                imageUrl = "http://example.com/image2.jpg",
                fallbackEmployeeName = "Jane Smith",
                timestamp = 311328000L,
            )
        ))
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getRecords(EmployeeId("employee-1"), PropertyId("property-1"))

        // Assert
        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertEquals(2, list?.size)
        assertEquals(EmployeeId("employee-1"), list?.get(0)?.employeePk)
        assertEquals(EmployeeId("employee-2"), list?.get(1)?.employeePk)
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `getAllRecords should return mapped time card records for property`() = runTest {
        // Arrange
        val networkResponse = TimeCardEventListNetworkResponse(listOf(
            TimeCardEventNetworkResponse(
                id = TimeCardEventId("tc-3"),
                employeeId = EmployeeId("employee-3"),
                propertyId = PropertyId("property-2"),
                type = TimeCardEventType.CLOCK_IN,
                imageUrl = "http://example.com/image3.jpg",
                fallbackEmployeeName = "Alice Johnson",
                timestamp = 411324800L,
            )
        ))
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getAllRecords(PropertyId("property-2"))

        // Assert
        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertEquals(1, list?.size)
        assertEquals(PropertyId("property-2"), list?.get(0)?.propertyId)
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `getRecord should return mapped time card record for id`() = runTest {
        // Arrange
        val timeCardId = TimeCardEventId("tc-1")
        val networkResponse = TimeCardEventNetworkResponse(
            id = timeCardId,
            employeeId = EmployeeId("employee-1"),
            propertyId = PropertyId("property-1"),
            type = TimeCardEventType.CLOCK_IN,
            imageUrl = "http://example.com/image1.jpg",
            fallbackEmployeeName = "John Doe",
            timestamp = 1610000000L,
        )
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getRecord(timeCardId)

        // Assert
        assertTrue(result.isSuccess)
        val record = result.getOrNull()
        assertEquals(EmployeeId("employee-1"), record?.employeePk)
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `addRecord should return mapped time card after creation`() = runTest {
        // Arrange
        val createRecord = TimeCardRecordModel(
            id = null,
            employeePk = EmployeeId("employee-4"),
            propertyId = PropertyId("property-3"),
            entityId = null,
            eventType = TimeCardEventType.CLOCK_OUT,
            imageUrl = null,
            eventTime = 1610033600L,
            imageRef = null,
        )
        val networkResponse = TimeCardEventNetworkResponse(
            id = TimeCardEventId("tc-4"),
            employeeId = EmployeeId("employee-4"),
            propertyId = PropertyId("property-3"),
            type = TimeCardEventType.CLOCK_OUT,
            imageUrl = null,
            fallbackEmployeeName = null,
            timestamp = 1610033600L,
        )
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.addRecord(createRecord)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(EmployeeId("employee-4"), result.getOrNull()?.employeePk)
    }
}
