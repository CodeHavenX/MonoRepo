@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType

val PROPERTY_1 = PropertyModel(
    id = PropertyId("property_id_1"),
    name = "Cenit",
    address = "123 Main St",
)

val PROPERTY_2 = PropertyModel(
    id = PropertyId("property_id_2"),
    name = "Vanguard2",
    address = "456 Elm St",
)

val STAFF_1 = StaffModel(
    id = StaffId("staff_id_1"),
    idType = IdType.DNI,
    name = "Antonio",
    lastName = "Banderas",
    role = StaffRole.ADMIN,
)

val STAFF_2 = StaffModel(
    id = StaffId("staff_id_2"),
    idType = IdType.PASSPORT,
    name = "Penelope",
    lastName = "Cruz",
    role = StaffRole.SECURITY,
)

val STAFF_3 = StaffModel(
    id = StaffId("staff_id_3"),
    idType = IdType.CE,
    name = "Javier",
    lastName = "Bardem",
    role = StaffRole.SECURITY,
)

val STAFF_4 = StaffModel(
    id = StaffId("staff_id_4"),
    idType = IdType.DNI,
    name = "Salma",
    lastName = "Hayek",
    role = StaffRole.SECURITY_COVER,
)

val EVENT_LOG_ENTRY_STAFF_1_1 = EventLogRecordModel(
    id = EventLogEntryId("event_log_entry_id_1"),
    staffPk = STAFF_1.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    eventType = EventLogEventType.MAINTENANCE_SERVICE,
    fallbackEventType = null,
    timeRecorded = 1727702654,
    title = "Routine Check",
    description = "Performed routine maintenance check.",
    unit = "Unit 101",
    entityId = null,
    attachments = emptyList(),
)

val EVENT_LOG_ENTRY_STAFF_1_2 = EventLogRecordModel(
    id = EventLogEntryId("event_log_entry_id_2"),
    staffPk = STAFF_1.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    eventType = EventLogEventType.INCIDENT,
    fallbackEventType = "Inspection",
    timeRecorded = 1727702655,
    title = "Monthly Inspection",
    description = "Performed monthly inspection.",
    unit = "Unit 202",
    entityId = null,
    attachments = emptyList(),
)

val EVENT_LOG_ENTRY_STAFF_2_1 = EventLogRecordModel(
    id = EventLogEntryId("event_log_entry_id_3"),
    staffPk = STAFF_2.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    eventType = EventLogEventType.MAINTENANCE_SERVICE,
    fallbackEventType = "General Maintenance",
    timeRecorded = 1727702656,
    title = "Routine Check",
    description = "Performed routine maintenance check.",
    unit = "Unit 101",
    entityId = null,
    attachments = emptyList(),
)

val EVENT_LOG_ENTRY_STAFF_3_1 = EventLogRecordModel(
    id = EventLogEntryId("event_log_entry_id_4"),
    staffPk = STAFF_3.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    eventType = EventLogEventType.INCIDENT,
    fallbackEventType = "Inspection",
    timeRecorded = 1727702657,
    title = "Monthly Inspection",
    description = "Performed monthly inspection.",
    unit = "Unit 202",
    entityId = null,
    attachments = emptyList(),
)

val EVENT_LOG_ENTRY_STAFF_4_1 = EventLogRecordModel(
    id = EventLogEntryId("event_log_entry_id_5"),
    staffPk = STAFF_4.id,
    fallbackStaffName = null,
    propertyId = PROPERTY_1.id,
    eventType = EventLogEventType.MAINTENANCE_SERVICE,
    fallbackEventType = "General Maintenance",
    timeRecorded = 1727702658,
    title = "Routine Check",
    description = "Performed routine maintenance check.",
    unit = "Unit 101",
    entityId = null,
    attachments = emptyList(),
)

val TIME_CARD_EVENT_1 = TimeCardRecordModel(
    id = TimeCardEventId("time_card_event_id_1"),
    staffPk = STAFF_1.id,
    propertyId = PROPERTY_1.id,
    eventType = TimeCardEventType.CLOCK_IN,
    imageUrl = "http://example.com/image1.jpg",
    eventTime = 1727702654,
    entityId = null,
    imageRef = null,
)

val TIME_CARD_EVENT_2 = TimeCardRecordModel(
    id = TimeCardEventId("time_card_event_id_2"),
    staffPk = STAFF_2.id,
    propertyId = PROPERTY_1.id,
    eventType = TimeCardEventType.CLOCK_OUT,
    imageUrl = "http://example.com/image2.jpg",
    eventTime = 1727702655,
    entityId = null,
    imageRef = null,
)

val TIME_CARD_EVENT_3 = TimeCardRecordModel(
    id = TimeCardEventId("time_card_event_id_3"),
    staffPk = STAFF_3.id,
    propertyId = PROPERTY_1.id,
    eventType = TimeCardEventType.CLOCK_IN,
    imageUrl = "http://example.com/image3.jpg",
    eventTime = 1727702656,
    entityId = null,
    imageRef = null,
)

val TIME_CARD_EVENT_4 = TimeCardRecordModel(
    id = TimeCardEventId("time_card_event_id_4"),
    staffPk = STAFF_4.id,
    propertyId = PROPERTY_1.id,
    eventType = TimeCardEventType.CLOCK_OUT,
    imageUrl = "http://example.com/image4.jpg",
    eventTime = 1727702657,
    entityId = null,
    imageRef = null,
)

val TIME_CARD_EVENT_5 = TimeCardRecordModel(
    id = TimeCardEventId("time_card_event_id_5"),
    staffPk = STAFF_1.id,
    propertyId = PROPERTY_1.id,
    eventType = TimeCardEventType.CLOCK_OUT,
    imageUrl = "http://example.com/image2.jpg",
    eventTime = 1727712654,
    entityId = null,
    imageRef = null,
)
