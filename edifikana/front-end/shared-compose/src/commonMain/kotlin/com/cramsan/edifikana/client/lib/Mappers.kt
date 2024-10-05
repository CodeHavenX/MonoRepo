package com.cramsan.edifikana.client.lib

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.TimeCardEventType
import edifikana_lib.Res
import edifikana_lib.event_type_delivery
import edifikana_lib.event_type_guest
import edifikana_lib.event_type_incident
import edifikana_lib.event_type_maintenance_service
import edifikana_lib.event_type_other
import edifikana_lib.id_type_ce
import edifikana_lib.id_type_dni
import edifikana_lib.id_type_other
import edifikana_lib.id_type_passport
import edifikana_lib.role_admin
import edifikana_lib.role_limpieza
import edifikana_lib.role_security
import edifikana_lib.role_security_cover
import edifikana_lib.role_unknown
import edifikana_lib.time_card_event_clock_in
import edifikana_lib.time_card_event_clock_out
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import java.time.format.DateTimeFormatter
import java.util.TimeZone

/**
 * Converts the [StaffRole] to a friendly string.
 */
suspend fun StaffRole?.toRoleFriendlyName(): String {
    return when (this) {
        StaffRole.ADMIN -> getString(Res.string.role_admin)
        StaffRole.SECURITY -> getString(Res.string.role_security)
        StaffRole.SECURITY_COVER -> getString(Res.string.role_security_cover)
        StaffRole.CLEANING -> getString(Res.string.role_limpieza)
        null -> getString(Res.string.role_unknown)
    }
}

/**
 * Converts the [StaffRole] to a friendly string.
 */
@Composable
fun StaffRole?.toRoleFriendlyNameCompose(): String {
    return when (this) {
        StaffRole.ADMIN -> stringResource(Res.string.role_admin)
        StaffRole.SECURITY -> stringResource(Res.string.role_security)
        StaffRole.SECURITY_COVER -> stringResource(Res.string.role_security_cover)
        StaffRole.CLEANING -> stringResource(Res.string.role_limpieza)
        null -> stringResource(Res.string.role_unknown)
    }
}

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

/**
 * Converts a [Long] timestamp to a friendly date time string.
 */
fun Long?.toFriendlyDateTime(): String {
    if (this == null) return ""

    val tz = kotlinx.datetime.TimeZone.of(TimeZone.getDefault().id)
    val instant = Instant.fromEpochSeconds(this)
    return dateFormatter.format(instant.toLocalDateTime(tz).toJavaLocalDateTime())
}

/**
 * Converts the [TimeCardEventType] to a friendly string.
 */
suspend fun TimeCardEventType?.eventTypeFriendlyName(): String {
    return when (this) {
        TimeCardEventType.CLOCK_IN -> getString(Res.string.time_card_event_clock_in)
        TimeCardEventType.CLOCK_OUT -> getString(Res.string.time_card_event_clock_out)
        TimeCardEventType.OTHER, null -> getString(Res.string.role_unknown)
    }
}

/**
 * Converts the [IdType] to a friendly string.
 */
@Composable
fun IdType.toIdTypeFriendlyName(): String {
    return when (this) {
        IdType.DNI -> stringResource(Res.string.id_type_dni)
        IdType.PASSPORT -> stringResource(Res.string.id_type_ce)
        IdType.CE -> stringResource(Res.string.id_type_passport)
        IdType.OTHER -> stringResource(Res.string.id_type_other)
    }
}

/**
 * Converts the [EventLogEventType] to a friendly string.
 */
suspend fun EventLogEventType?.toFriendlyString(): String {
    return when (this) {
        EventLogEventType.GUEST -> getString(Res.string.event_type_guest)
        EventLogEventType.DELIVERY -> getString(Res.string.event_type_delivery)
        EventLogEventType.INCIDENT -> getString(Res.string.event_type_incident)
        EventLogEventType.MAINTENANCE_SERVICE -> getString(Res.string.event_type_maintenance_service)
        else -> getString(Res.string.event_type_other)
    }
}

/**
 * Converts the [EventLogEventType] to a friendly string.
 */
@Composable
fun EventLogEventType?.toFriendlyStringCompose(): String {
    return when (this) {
        EventLogEventType.GUEST -> stringResource(Res.string.event_type_guest)
        EventLogEventType.DELIVERY -> stringResource(Res.string.event_type_delivery)
        EventLogEventType.INCIDENT -> stringResource(Res.string.event_type_incident)
        EventLogEventType.MAINTENANCE_SERVICE -> stringResource(Res.string.event_type_maintenance_service)
        else -> stringResource(Res.string.event_type_other)
    }
}
