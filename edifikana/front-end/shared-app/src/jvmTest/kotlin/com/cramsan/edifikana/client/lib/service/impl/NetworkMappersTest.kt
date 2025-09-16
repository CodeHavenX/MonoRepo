package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole

import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.AuthMetadataNetworkResponse
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.StaffNetworkResponse
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
     * Tests that the [StaffNetworkResponse] is correctly mapped to [StaffModel].
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `toTimeCardRecordModel maps all fields correctly`() {
        // Arrange
        val networkResponse = TimeCardEventNetworkResponse(
            id = "event-123",
            staffId = "staff-456",
            propertyId = "property-789",
            type = TimeCardEventType.CLOCK_IN,
            timestamp = 1720000000L,
            imageUrl = "http://image.url",
            fallbackStaffName = "Jenny"
        )

        // Act
        val model = networkResponse.toTimeCardRecordModel()

        // Assert
        assertEquals(TimeCardEventId("event-123"), model.id)
        assertEquals(StaffId("staff-456"), model.staffPk)
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
            id = "eventlog-1",
            staffId = "staff-1",
            propertyId = "property-1",
            timestamp = 123456789L,
            unit = "unit-1",
            type = EventLogEventType.INCIDENT,
            fallbackEventType = "fallbackType",
            fallbackStaffName = "fallbackName",
            title = "Test Title",
            description = "Test Description"
        )

        // Act
        val model = networkResponse.toEventLogRecordModel()

        // Assert
        assertEquals(EventLogEntryId("eventlog-1"), model.id)
        assertEquals(StaffId("staff-1"), model.staffPk)
        assertEquals(PropertyId("property-1"), model.propertyId)
        assertEquals(123456789L, model.timeRecorded)
        assertEquals("unit-1", model.unit)
        assertEquals(EventLogEventType.INCIDENT, model.eventType)
        assertEquals("fallbackType", model.fallbackStaffName)
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
            staffPk = StaffId("staff-2"),
            propertyId = PropertyId("property-2"),
            timeRecorded = 987654321L,
            unit = "unit-2",
            eventType = EventLogEventType.MAINTENANCE_SERVICE,
            fallbackStaffName = "fallbackName2",
            fallbackEventType = "fallbackType2",
            title = "Title2",
            description = "Description2",
            attachments = mockk(),
        )

        // Act
        val request = model.toCreateEventLogEntryNetworkRequest()

        // Assert
        assertEquals("staff-2", request.staffId)
        assertEquals("fallbackName2", request.fallbackStaffName)
        assertEquals("property-2", request.propertyId)
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
            staffPk = StaffId("staff-3"),
            propertyId = PropertyId("property-3"),
            timeRecorded = 123123123L,
            unit = "unit-3",
            eventType = EventLogEventType.DELIVERY,
            fallbackStaffName = "fallbackName3",
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
     * Tests that the [CreateStaffRequest] is correctly mapped to [CreateStaffNetworkRequest].
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `toCreateStaffNetworkRequest maps all fields correctly`() {
        // Arrange
        val createStaffRequest = StaffModel.CreateStaffRequest(
            idType = IdType.CE,
            firstName = "John",
            lastName = "Doe",
            role = StaffRole.MANAGER,
            propertyId = PropertyId("property-4")
        )

        // Act
        val request = createStaffRequest.toCreateStaffNetworkRequest()

        // Assert
        assertEquals(IdType.CE, request.idType)
        assertEquals("John", request.firstName)
        assertEquals("Doe", request.lastName)
        assertEquals(StaffRole.MANAGER, request.role)
        assertEquals("property-4", request.propertyId)
    }

    /**
     * Tests that the [StaffNetworkResponse] is correctly mapped to [StaffModel].
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `toStaffModel maps all fields correctly`() {
        // Arrange
        val staffNetworkResponse = StaffNetworkResponse(
            id = "staff-5",
            firstName = "Jane",
            lastName = "Smith",
            role = StaffRole.SECURITY,
            idType = IdType.DNI,
            propertyId = "Cenit",
        )

        // Act
        val model = staffNetworkResponse.toStaffModel()

        // Assert
        assertEquals(StaffId("staff-5"), model.id)
        assertEquals("Jane", model.firstName)
        assertEquals("Smith", model.lastName)
        assertEquals(StaffRole.SECURITY, model.role)
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
            staffPk = StaffId("staff-6"),
            propertyId = PropertyId("property-6"),
            eventType = TimeCardEventType.CLOCK_OUT,
            eventTime = 0L,
            imageUrl = "http://image.url/6",
            imageRef = null
        )

        // Act
        val request = model.toCreateTimeCardEventNetworkRequest()

        // Assert
        assertEquals("staff-6", request.staffId)
        assertEquals("", request.fallbackStaffName)
        assertEquals(TimeCardEventType.CLOCK_OUT, request.type)
        assertEquals("property-6", request.propertyId)
        assertEquals("http://image.url/6", request.imageUrl)
    }
}
