@file:Suppress("MagicNumber")

package com.cramsan.edifikana.server.core.repository.dummy

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.User
import kotlinx.datetime.Instant

val USER_1 = User(
    id = UserId("user_id_1"),
    email = "user1@test.com",
    hasGlobalPerms = false,
)

val USER_2 = User(
    id = UserId("user_id_2"),
    email = "user2@test.com",
    hasGlobalPerms = false,
)

val USER_3 = User(
    id = UserId("user_id_3"),
    email = "user3@test.com",
    hasGlobalPerms = false,
)

val USER_4 = User(
    id = UserId("user_id_4"),
    email = "user4@test.com",
    hasGlobalPerms = true,
)

val PROPERTY_1 = Property(
    id = PropertyId("property_id_1"),
    name = "Cenit",
)

val PROPERTY_2 = Property(
    id = PropertyId("property_id_2"),
    name = "Vanguard2",
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
