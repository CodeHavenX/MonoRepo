@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.data

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType

val PROPERTY_1 = PropertyModel(
    id = PropertyId("property_id_1"),
    name = "Cenit",
    address = "123 Main St",
    organizationId = OrganizationId("org_id_1"),
)

val PROPERTY_2 = PropertyModel(
    id = PropertyId("property_id_2"),
    name = "Vanguard2",
    address = "456 Elm St",
    organizationId = OrganizationId("org_id_1"),
)

val EMPLOYEE_1 = EmployeeModel(
    id = EmployeeId("employee_id_1"),
    idType = IdType.DNI,
    firstName = "Antonio",
    lastName = "Banderas",
    role = EmployeeRole.MANAGER,
    email = "antonio.banderas@gmail.com",
)

val EMPLOYEE_2 = EmployeeModel(
    id = EmployeeId("employee_id_2"),
    idType = IdType.PASSPORT,
    firstName = "Penelope",
    lastName = "Cruz",
    role = EmployeeRole.SECURITY,
    email = "p.cruz@yahoo.com",
)

val EMPLOYEE_3 = EmployeeModel(
    id = EmployeeId("employee_id_3"),
    idType = IdType.CE,
    firstName = "Javier",
    lastName = "Bardem",
    role = EmployeeRole.SECURITY,
    email = null,
)

val EMPLOYEE_4 = EmployeeModel(
    id = EmployeeId("employee_id_4"),
    idType = IdType.DNI,
    firstName = "Salma",
    lastName = "Hayek",
    role = EmployeeRole.SECURITY_COVER,
    email = "sal_ha@hotmail.com",
)

val EVENT_LOG_ENTRY_EMPLOYEE_1_1 = EventLogRecordModel(
    id = EventLogEntryId("event_log_entry_id_1"),
    employeePk = EMPLOYEE_1.id,
    fallbackEmployeeName = null,
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

val EVENT_LOG_ENTRY_EMPLOYEE_1_2 = EventLogRecordModel(
    id = EventLogEntryId("event_log_entry_id_2"),
    employeePk = EMPLOYEE_1.id,
    fallbackEmployeeName = null,
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

val EVENT_LOG_ENTRY_EMPLOYEE_2_1 = EventLogRecordModel(
    id = EventLogEntryId("event_log_entry_id_3"),
    employeePk = EMPLOYEE_2.id,
    fallbackEmployeeName = null,
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

val EVENT_LOG_ENTRY_EMPLOYEE_3_1 = EventLogRecordModel(
    id = EventLogEntryId("event_log_entry_id_4"),
    employeePk = EMPLOYEE_3.id,
    fallbackEmployeeName = null,
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

val EVENT_LOG_ENTRY_EMPLOYEE_4_1 = EventLogRecordModel(
    id = EventLogEntryId("event_log_entry_id_5"),
    employeePk = EMPLOYEE_4.id,
    fallbackEmployeeName = null,
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
    employeePk = EMPLOYEE_1.id,
    propertyId = PROPERTY_1.id,
    eventType = TimeCardEventType.CLOCK_IN,
    imageUrl = "http://example.com/image1.jpg",
    eventTime = 1727702654,
    entityId = null,
    imageRef = null,
)

val TIME_CARD_EVENT_2 = TimeCardRecordModel(
    id = TimeCardEventId("time_card_event_id_2"),
    employeePk = EMPLOYEE_2.id,
    propertyId = PROPERTY_1.id,
    eventType = TimeCardEventType.CLOCK_OUT,
    imageUrl = "http://example.com/image2.jpg",
    eventTime = 1727702655,
    entityId = null,
    imageRef = null,
)

val TIME_CARD_EVENT_3 = TimeCardRecordModel(
    id = TimeCardEventId("time_card_event_id_3"),
    employeePk = EMPLOYEE_3.id,
    propertyId = PROPERTY_1.id,
    eventType = TimeCardEventType.CLOCK_IN,
    imageUrl = "http://example.com/image3.jpg",
    eventTime = 1727702656,
    entityId = null,
    imageRef = null,
)

val TIME_CARD_EVENT_4 = TimeCardRecordModel(
    id = TimeCardEventId("time_card_event_id_4"),
    employeePk = EMPLOYEE_4.id,
    propertyId = PROPERTY_1.id,
    eventType = TimeCardEventType.CLOCK_OUT,
    imageUrl = "http://example.com/image4.jpg",
    eventTime = 1727702657,
    entityId = null,
    imageRef = null,
)

val TIME_CARD_EVENT_5 = TimeCardRecordModel(
    id = TimeCardEventId("time_card_event_id_5"),
    employeePk = EMPLOYEE_1.id,
    propertyId = PROPERTY_1.id,
    eventType = TimeCardEventType.CLOCK_OUT,
    imageUrl = "http://example.com/image2.jpg",
    eventTime = 1727712654,
    entityId = null,
    imageRef = null,
)
