package com.cramsan.edifikana.client.lib

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.utils.time.Chronos
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

/**
 * Converts the [StaffRole] to a friendly string.
 */
suspend fun StaffRole?.toRoleFriendlyName(
    stringProvider: StringProvider,
): String {
    return when (this) {
        StaffRole.ADMIN -> stringProvider.getString(Res.string.role_admin)
        StaffRole.SECURITY -> stringProvider.getString(Res.string.role_security)
        StaffRole.SECURITY_COVER -> stringProvider.getString(Res.string.role_security_cover)
        StaffRole.CLEANING -> stringProvider.getString(Res.string.role_limpieza)
        null -> stringProvider.getString(Res.string.role_unknown)
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

private val dateTimeFormatter = LocalDateTime.Formats.ISO

/**
 * Converts a [Long] timestamp to a friendly date time string.
 */
fun Long?.toFriendlyDateTime(): String {
    if (this == null) return ""

    val tz = Chronos.timeZone()
    val instant = Instant.fromEpochSeconds(this)
    return dateTimeFormatter.format(instant.toLocalDateTime(tz))
}

/**
 * Converts the [TimeCardEventType] to a friendly string.
 */
suspend fun TimeCardEventType?.eventTypeFriendlyName(
    stringProvider: StringProvider,
): String {
    return when (this) {
        TimeCardEventType.CLOCK_IN -> stringProvider.getString(Res.string.time_card_event_clock_in)
        TimeCardEventType.CLOCK_OUT -> stringProvider.getString(Res.string.time_card_event_clock_out)
        TimeCardEventType.OTHER, null -> stringProvider.getString(Res.string.role_unknown)
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
suspend fun EventLogEventType?.toFriendlyString(
    stringProvider: StringProvider,
): String {
    return when (this) {
        EventLogEventType.GUEST -> stringProvider.getString(Res.string.event_type_guest)
        EventLogEventType.DELIVERY -> stringProvider.getString(Res.string.event_type_delivery)
        EventLogEventType.INCIDENT -> stringProvider.getString(Res.string.event_type_incident)
        EventLogEventType.MAINTENANCE_SERVICE -> stringProvider.getString(Res.string.event_type_maintenance_service)
        else -> stringProvider.getString(Res.string.event_type_other)
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
