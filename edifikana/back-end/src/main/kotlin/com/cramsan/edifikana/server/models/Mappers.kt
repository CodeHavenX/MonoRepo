@file:OptIn(FireStoreModel::class)

package com.cramsan.edifikana.server.models

import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventType
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.IdType
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
import com.cramsan.edifikana.lib.firestore.helpers.eventTypeFriendlyName
import com.cramsan.edifikana.lib.firestore.helpers.toFriendlyDateTime
import com.cramsan.edifikana.lib.firestore.helpers.toFriendlyString
import com.google.cloud.firestore.DocumentSnapshot
import kotlinx.datetime.TimeZone

fun DocumentSnapshot.toEmployee(): Employee {
    return Employee(
        this.getString("id"),
        this.getString("idType")?.let { IdType.fromString(it) } ?: IdType.OTHER,
        this.getString("name"),
        this.getString("lastName"),
        EmployeeRole.fromString(this.getString("role")),
    )
}

fun DocumentSnapshot.toEventLogRecord(): EventLogRecord {
    return EventLogRecord(
        this.getString("employeeDocumentId"),
        this.getLong("timeRecorded"),
        this.getString("unit"),
        this.getString("eventType")?.let { EventType.fromString(it) } ?: EventType.OTHER,
        this.getString("fallbackEmployeeName"),
        this.getString("fallbackEventType"),
        this.getString("summary"),
        this.getString("description"),
    )
}

fun DocumentSnapshot.toTimeCardEvent(): TimeCardRecord {
    return TimeCardRecord(
        this.getString("employeeDocumentId"),
        this.getString("eventType")?.let { TimeCardEventType.fromString(it) } ?: TimeCardEventType.OTHER,
        this.getLong("eventTime"),
        this.getString("imageUrl"),
    )
}

fun TimeCardRecord.toRowEntry(
    employeeFullName: String,
    imageUrlOverride: String,
): List<String> {
    return listOf(
        employeeFullName,
        (eventType ?: TimeCardEventType.OTHER).eventTypeFriendlyName(),
        eventTime.toFriendlyDateTime(TimeZone.of("America/Lima")),
        imageUrlOverride,
    )
}

fun EventLogRecord.toRowEntry(
    employeeFullName: String,
    attachments: String,
): List<String> {
    return listOf(
        employeeFullName,
        timeRecorded.toFriendlyDateTime(TimeZone.of("America/Lima")),
        unit.orEmpty(),
        (eventType ?: EventType.OTHER).toFriendlyString(),
        fallbackEmployeeName.orEmpty(),
        fallbackEventType.orEmpty(),
        summary.orEmpty(),
        description.orEmpty(),
        attachments,
    )
}
