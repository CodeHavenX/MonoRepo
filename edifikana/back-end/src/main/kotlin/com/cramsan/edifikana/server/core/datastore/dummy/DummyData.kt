@file:Suppress("MagicNumber")
@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.core.datastore.dummy

import com.cramsan.edifikana.lib.model.AssetId
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.Asset
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.User
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

val USER_1 = User(
    id = UserId("user_id_1"),
    email = "user1@test.com",
    phoneNumber = "1234567890",
    firstName = "John",
    lastName = "Doe",
    authMetadata = User.AuthMetadata(isPasswordSet = false),
)

val USER_2 = User(
    id = UserId("user_id_2"),
    email = "user2@test.com",
    phoneNumber = "0987654321",
    firstName = "Jane",
    lastName = "Smith",
    authMetadata = User.AuthMetadata(isPasswordSet = false),
)

val USER_3 = User(
    id = UserId("user_id_3"),
    email = "user3@test.com",
    phoneNumber = "1122334455",
    firstName = "Alice",
    lastName = "Johnson",
    authMetadata = User.AuthMetadata(isPasswordSet = false),
)

val USER_4 = User(
    id = UserId("user_id_4"),
    email = "user4@test.com",
    phoneNumber = "5566778899",
    firstName = "Bob",
    lastName = "Brown",
    authMetadata = User.AuthMetadata(isPasswordSet = false),
)

val PROPERTY_1 = Property(
    id = PropertyId("property_id_1"),
    name = "Cenit",
    address = "123 Main St, Springfield, USA",
)

val PROPERTY_2 = Property(
    id = PropertyId("property_id_2"),
    name = "Vanguard2",
    address = "456 Elm St, Springfield, USA",
)

val STAFF_1 = Staff(
    id = StaffId("staff_id_1"),
    idType = IdType.DNI,
    firstName = "Antonio",
    lastName = "Banderas",
    role = StaffRole.ADMIN,
    propertyId = PropertyId("property_id_1"),
)

val STAFF_2 = Staff(
    id = StaffId("staff_id_2"),
    idType = IdType.PASSPORT,
    firstName = "Penelope",
    lastName = "Cruz",
    role = StaffRole.SECURITY,
    propertyId = PropertyId("property_id_1"),
)

val STAFF_3 = Staff(
    id = StaffId("staff_id_3"),
    idType = IdType.CE,
    firstName = "Javier",
    lastName = "Bardem",
    role = StaffRole.SECURITY,
    propertyId = PropertyId("property_id_1"),
)

val STAFF_4 = Staff(
    id = StaffId("staff_id_4"),
    idType = IdType.DNI,
    firstName = "Salma",
    lastName = "Hayek",
    role = StaffRole.SECURITY_COVER,
    propertyId = PropertyId("property_id_1"),
)

val EVENT_LOG_ENTRY_STAFF_1_1 = EventLogEntry(
    id = EventLogEntryId("event_log_entry_id_1"),
    staffId = STAFF_1.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    type = EventLogEventType.MAINTENANCE_SERVICE,
    fallbackEventType = null,
    timestamp = Instant.fromEpochSeconds(1727702654),
    title = "Routine Check",
    description = "Performed routine maintenance check.",
    unit = "Unit 101",
)

val EVENT_LOG_ENTRY_STAFF_1_2 = EventLogEntry(
    id = EventLogEntryId("event_log_entry_id_2"),
    staffId = STAFF_1.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    type = EventLogEventType.INCIDENT,
    fallbackEventType = "Inspection",
    timestamp = Instant.fromEpochSeconds(1727702655),
    title = "Monthly Inspection",
    description = "Performed monthly inspection.",
    unit = "Unit 202",
)

val EVENT_LOG_ENTRY_STAFF_2_1 = EventLogEntry(
    id = EventLogEntryId("event_log_entry_id_3"),
    staffId = STAFF_2.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    type = EventLogEventType.MAINTENANCE_SERVICE,
    fallbackEventType = "General Maintenance",
    timestamp = Instant.fromEpochSeconds(1727702656),
    title = "Routine Check",
    description = "Performed routine maintenance check.",
    unit = "Unit 101",
)

val EVENT_LOG_ENTRY_STAFF_3_1 = EventLogEntry(
    id = EventLogEntryId("event_log_entry_id_4"),
    staffId = STAFF_3.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    type = EventLogEventType.INCIDENT,
    fallbackEventType = "Inspection",
    timestamp = Instant.fromEpochSeconds(1727702657),
    title = "Monthly Inspection",
    description = "Performed monthly inspection.",
    unit = "Unit 202",
)

val EVENT_LOG_ENTRY_STAFF_4_1 = EventLogEntry(
    id = EventLogEntryId("event_log_entry_id_5"),
    staffId = STAFF_4.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    type = EventLogEventType.MAINTENANCE_SERVICE,
    fallbackEventType = "General Maintenance",
    timestamp = Instant.fromEpochSeconds(1727702658),
    title = "Routine Check",
    description = "Performed routine maintenance check.",
    unit = "Unit 101",
)

val TIME_CARD_EVENT_1 = TimeCardEvent(
    id = TimeCardEventId("time_card_event_id_1"),
    staffId = STAFF_1.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    type = TimeCardEventType.CLOCK_IN,
    imageUrl = "http://example.com/image1.jpg",
    timestamp = Instant.fromEpochSeconds(1727702654),
)

val TIME_CARD_EVENT_2 = TimeCardEvent(
    id = TimeCardEventId("time_card_event_id_2"),
    staffId = STAFF_2.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    type = TimeCardEventType.CLOCK_OUT,
    imageUrl = "http://example.com/image2.jpg",
    timestamp = Instant.fromEpochSeconds(1727702655),
)

val TIME_CARD_EVENT_3 = TimeCardEvent(
    id = TimeCardEventId("time_card_event_id_3"),
    staffId = STAFF_3.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    type = TimeCardEventType.CLOCK_IN,
    imageUrl = "http://example.com/image3.jpg",
    timestamp = Instant.fromEpochSeconds(1727702656),
)

val TIME_CARD_EVENT_4 = TimeCardEvent(
    id = TimeCardEventId("time_card_event_id_4"),
    staffId = STAFF_4.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    type = TimeCardEventType.CLOCK_OUT,
    imageUrl = "http://example.com/image4.jpg",
    timestamp = Instant.fromEpochSeconds(1727702657),
)

val TIME_CARD_EVENT_5 = TimeCardEvent(
    id = TimeCardEventId("time_card_event_id_5"),
    staffId = STAFF_1.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    type = TimeCardEventType.CLOCK_OUT,
    imageUrl = "http://example.com/image2.jpg",
    timestamp = Instant.fromEpochSeconds(1727712654),
)

val ASSET_1 = Asset(
    id = AssetId("time_card_event_id_5"),
    fileName = "staff1.png",
    signedUrl = null,
    content = byteArrayOf(
        // PNG file header bytes
        0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
        // Minimal PNG IHDR chunk (not a valid image, but enough for dummy)
        0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
        0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
        0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, 0xC4.toByte(), 0x89.toByte(),
        // IDAT chunk header
        0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54,
        0x78, 0x9C.toByte(), 0x63, 0x60, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01,
        // IEND chunk
        0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, 0xAE.toByte(), 0x42, 0x60, 0x82.toByte()
    )
)

val ASSET_2 = Asset(
    id = AssetId("time_card_event_id_6"),
    fileName = "staff2.png",
    signedUrl = null,
    content = byteArrayOf(
        // PNG file header bytes
        0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
        // Minimal PNG IHDR chunk (not a valid image, but enough for dummy)
        0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
        0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x02,
        0x08, 0x06, 0x00, 0x00, 0x00, 0xF4.toByte(), 0x78, 0xD4.toByte(), 0xFA.toByte(),
        // IDAT chunk header
        0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54,
        0x78, 0x9C.toByte(), 0x63, 0x60, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01,
        // IEND chunk
        0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, 0xAE.toByte(), 0x42, 0x60, 0x82.toByte()
    )
)
