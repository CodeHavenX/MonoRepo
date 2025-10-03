package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole

import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.AuthMetadataNetworkResponse
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.EmployeeNetworkResponse
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for the NetworkMappers that convert network responses to domain models.
 */
class NetworkMappersTest {
    /**
     * Tests that the [EmployeeNetworkResponse] is correctly mapped to [EmployeeModel].
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `toTimeCardRecordModel maps all fields correctly`() {
        // Arrange
        val networkResponse = TimeCardEventNetworkResponse(
            id = TimeCardEventId("event-123"),
            employeeId = EmployeeId("emp-456"),
            propertyId = PropertyId("property-789"),
            type = TimeCardEventType.CLOCK_IN,
            timestamp = 1720000000L,
            imageUrl = "http://image.url",
            fallbackEmployeeName = "Jenny"
        )

        // Act
        val model = networkResponse.toTimeCardRecordModel()

        // Assert
        assertEquals(TimeCardEventId("event-123"), model.id)
        assertEquals(EmployeeId("emp-456"), model.employeePk)
        assertEquals(PropertyId("property-789"), model.propertyId)
        assertEquals(TimeCardEventType.CLOCK_IN, model.eventType)
        assertEquals(1720000000L, model.eventTime)
        assertEquals("http://image.url", model.imageUrl)
        assertEquals(null, model.entityId)
        assertEquals(null, model.imageRef)
    }

    /**
     * Tests that the [UserNetworkResponse] is correctly mapped to [UserModel].
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `toUserModel maps all fields correctly`() {
        // Arrange
        val networkResponse = UserNetworkResponse(
            id = "user-123",
            email = "test@example.com",
            phoneNumber = "555-1234",
            firstName = "John",
            lastName = "Doe",
            authMetadata = AuthMetadataNetworkResponse(
                isPasswordSet = true,
            ),
        )

        // Act
        val model = networkResponse.toUserModel()

        // Assert
        assertEquals(UserId("user-123"), model.id)
        assertEquals("test@example.com", model.email)
        assertEquals("555-1234", model.phoneNumber)
        assertEquals("John", model.firstName)
        assertEquals("Doe", model.lastName)
        assertEquals(true, model.authMetadata?.isPasswordSet)
    }

    /**
     * Tests that the [EventLogEntryNetworkResponse] is correctly mapped to [EventLogRecordModel].
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `toEventLogRecordModel maps all fields correctly`() {
        // Arrange
        val networkResponse = EventLogEntryNetworkResponse(
            id = EventLogEntryId("eventlog-1"),
            employeeId = EmployeeId("emp-1"),
            propertyId = PropertyId("property-1"),
            timestamp = 123456789L,
            unit = "unit-1",
            type = EventLogEventType.INCIDENT,
            fallbackEventType = "fallbackType",
            fallbackEmployeeName = "fallbackName",
            title = "Test Title",
            description = "Test Description"
        )

        // Act
        val model = networkResponse.toEventLogRecordModel()

        // Assert
        assertEquals(EventLogEntryId("eventlog-1"), model.id)
        assertEquals(EmployeeId("emp-1"), model.employeePk)
        assertEquals(PropertyId("property-1"), model.propertyId)
        assertEquals(123456789L, model.timeRecorded)
        assertEquals("unit-1", model.unit)
        assertEquals(EventLogEventType.INCIDENT, model.eventType)
        assertEquals("fallbackType", model.fallbackEmployeeName)
        assertEquals("fallbackName", model.fallbackEventType)
        assertEquals("Test Title", model.title)
        assertEquals("Test Description", model.description)
    }

    /**
     * Tests that the [EventLogRecordModel] is correctly mapped to [CreateEventLogEntryNetworkRequest].
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `toCreateEventLogEntryNetworkRequest maps all fields correctly`() {
        // Arrange
        val model = EventLogRecordModel(
            id = EventLogEntryId("eventlog-2"),
            entityId = null,
            employeePk = EmployeeId("emp-2"),
            propertyId = PropertyId("property-2"),
            timeRecorded = 987654321L,
            unit = "unit-2",
            eventType = EventLogEventType.MAINTENANCE_SERVICE,
            fallbackEmployeeName = "fallbackName2",
            fallbackEventType = "fallbackType2",
            title = "Title2",
            description = "Description2",
            attachments = mockk(),
        )

        // Act
        val request = model.toCreateEventLogEntryNetworkRequest()

        // Assert
        assertEquals("emp-2", request.employeeId?.empId)
        assertEquals("fallbackName2", request.fallbackEmployeeName)
        assertEquals("property-2", request.propertyId.propertyId)
        assertEquals(EventLogEventType.MAINTENANCE_SERVICE, request.type)
        assertEquals("fallbackType2", request.fallbackEventType)
        assertEquals(987654321L, request.timestamp)
        assertEquals("Title2", request.title)
        assertEquals("Description2", request.description)
        assertEquals("unit-2", request.unit)
    }

    /**
     * Tests that the [EventLogRecordModel] is correctly mapped to [UpdateEventLogEntryNetworkRequest].
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `toUpdateEventLogEntryNetworkRequest maps all fields correctly`() {
        // Arrange
        val model = EventLogRecordModel(
            id = EventLogEntryId("eventlog-3"),
            entityId = null,
            employeePk = EmployeeId("emp-3"),
            propertyId = PropertyId("property-3"),
            timeRecorded = 123123123L,
            unit = "unit-3",
            eventType = EventLogEventType.DELIVERY,
            fallbackEmployeeName = "fallbackName3",
            fallbackEventType = "fallbackType3",
            title = "Title3",
            description = "Description3",
            attachments = emptyList(),
        )

        // Act
        val request = model.toUpdateEventLogEntryNetworkRequest()

        // Assert
        assertEquals(EventLogEventType.DELIVERY, request.type)
        assertEquals("fallbackType3", request.fallbackEventType)
        assertEquals("Title3", request.title)
        assertEquals("Description3", request.description)
        assertEquals("unit-3", request.unit)
    }

    /**
     * Tests that the [CreateEmployeeRequest] is correctly mapped to [CreateEmployeeNetworkRequest].
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `toCreateEmployeeNetworkRequest maps all fields correctly`() {
        // Arrange
        val createEmployeeRequest = EmployeeModel.CreateEmployeeRequest(
            idType = IdType.CE,
            firstName = "John",
            lastName = "Doe",
            role = EmployeeRole.MANAGER,
            propertyId = PropertyId("property-4")
        )

        // Act
        val request = createEmployeeRequest.toCreateEmployeeNetworkRequest()

        // Assert
        assertEquals(IdType.CE, request.idType)
        assertEquals("John", request.firstName)
        assertEquals("Doe", request.lastName)
        assertEquals(EmployeeRole.MANAGER, request.role)
        assertEquals("property-4", request.propertyId.propertyId)
    }

    /**
     * Tests that the [EmployeeNetworkResponse] is correctly mapped to [EmployeeModel].
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `toEmployeeModel maps all fields correctly`() {
        // Arrange
        val employeeNetworkResponse = EmployeeNetworkResponse(
            id = EmployeeId("emp-5"),
            firstName = "Jane",
            lastName = "Smith",
            role = EmployeeRole.SECURITY,
            idType = IdType.DNI,
            propertyId = PropertyId("Cenit"),
        )

        // Act
        val model = employeeNetworkResponse.toEmployeeModel()

        // Assert
        assertEquals(EmployeeId("emp-5"), model.id)
        assertEquals("Jane", model.firstName)
        assertEquals("Smith", model.lastName)
        assertEquals(EmployeeRole.SECURITY, model.role)
        assertEquals(IdType.DNI, model.idType)
        assertEquals(null, model.email)
    }

    /**
     * Tests that the [TimeCardRecordModel] is correctly mapped to [CreateTimeCardEventNetworkRequest].
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `toCreateTimeCardEventNetworkRequest maps all fields correctly`() {
        // Arrange
        val model = TimeCardRecordModel(
            id = TimeCardEventId("event-6"),
            entityId = null,
            employeePk = EmployeeId("emp-6"),
            propertyId = PropertyId("property-6"),
            eventType = TimeCardEventType.CLOCK_OUT,
            eventTime = 0L,
            imageUrl = "http://image.url/6",
            imageRef = null
        )

        // Act
        val request = model.toCreateTimeCardEventNetworkRequest()

        // Assert
        assertEquals("emp-6", request.employeeId.empId)
        assertEquals("", request.fallbackEmployeeName)
        assertEquals(TimeCardEventType.CLOCK_OUT, request.type)
        assertEquals("property-6", request.propertyId.propertyId)
        assertEquals("http://image.url/6", request.imageUrl)
    }
}
