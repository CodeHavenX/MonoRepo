package com.cramsan.edifikana.client.lib

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.firestore.EventType
import com.cramsan.edifikana.lib.firestore.IdType
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
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
import edifikana_lib.sign_in
import edifikana_lib.time_card_event_clock_in
import edifikana_lib.time_card_event_clock_out
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import java.time.format.DateTimeFormatter
import java.util.TimeZone

val signInString
    @Composable
    get() = stringResource(Res.string.sign_in)

suspend fun EmployeeRole?.toRoleFriendlyName(): String {
    return when (this) {
        EmployeeRole.ADMIN -> getString(Res.string.role_admin)
        EmployeeRole.SECURITY -> getString(Res.string.role_security)
        EmployeeRole.SECURITY_COVER -> getString(Res.string.role_security_cover)
        EmployeeRole.CLEANING -> getString(Res.string.role_limpieza)
        null -> getString(Res.string.role_unknown)
    }
}

@Composable
fun EmployeeRole?.toRoleFriendlyNameCompose(): String {
    return when (this) {
        EmployeeRole.ADMIN -> stringResource(Res.string.role_admin)
        EmployeeRole.SECURITY -> stringResource(Res.string.role_security)
        EmployeeRole.SECURITY_COVER -> stringResource(Res.string.role_security_cover)
        EmployeeRole.CLEANING -> stringResource(Res.string.role_limpieza)
        null -> stringResource(Res.string.role_unknown)
    }
}

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun Long?.toFriendlyDateTime(): String {
    if (this == null) return ""

    val tz = kotlinx.datetime.TimeZone.of(TimeZone.getDefault().id)
    val instant = Instant.fromEpochSeconds(this)
    return dateFormatter.format(instant.toLocalDateTime(tz).toJavaLocalDateTime())
}

suspend fun TimeCardEventType?.eventTypeFriendlyName(): String {
    return when (this) {
        TimeCardEventType.CLOCK_IN -> getString(Res.string.time_card_event_clock_in)
        TimeCardEventType.CLOCK_OUT -> getString(Res.string.time_card_event_clock_out)
        TimeCardEventType.OTHER, null -> getString(Res.string.role_unknown)
    }
}

@Composable
fun IdType.toIdTypeFriendlyName(): String {
    return when (this) {
        IdType.DNI -> stringResource(Res.string.id_type_dni)
        IdType.PASSPORT -> stringResource(Res.string.id_type_ce)
        IdType.CE -> stringResource(Res.string.id_type_passport)
        IdType.OTHER -> stringResource(Res.string.id_type_other)
    }
}

suspend fun EventType?.toFriendlyString(): String {
    return when (this) {
        EventType.GUEST -> getString(Res.string.event_type_guest)
        EventType.DELIVERY -> getString(Res.string.event_type_delivery)
        EventType.INCIDENT -> getString(Res.string.event_type_incident)
        EventType.MAINTENANCE_SERVICE -> getString(Res.string.event_type_maintenance_service)
        else -> getString(Res.string.event_type_other)
    }
}

@Composable
fun EventType?.toFriendlyStringCompose(): String {
    return when (this) {
        EventType.GUEST -> stringResource(Res.string.event_type_guest)
        EventType.DELIVERY -> stringResource(Res.string.event_type_delivery)
        EventType.INCIDENT -> stringResource(Res.string.event_type_incident)
        EventType.MAINTENANCE_SERVICE -> stringResource(Res.string.event_type_maintenance_service)
        else -> stringResource(Res.string.event_type_other)
    }
}
