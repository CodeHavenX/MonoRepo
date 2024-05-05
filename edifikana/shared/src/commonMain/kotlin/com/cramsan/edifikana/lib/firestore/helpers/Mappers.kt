package com.cramsan.edifikana.lib.firestore.helpers

import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.firestore.EventType
import com.cramsan.edifikana.lib.firestore.IdType
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime

fun Employee.fullName() = "$name $lastName".trim()

fun EmployeeRole?.toRoleFriendlyName(): String {
    return when (this) {
        EmployeeRole.ADMIN -> "Administrator"
        EmployeeRole.SECURITY -> "Seguridad"
        EmployeeRole.SECURITY_COVER -> "Descansero"
        EmployeeRole.CLEANING -> "Limpieza"
        null -> "Desconocido"
    }
}

val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun Long?.toFriendlyDateTime(tz: kotlinx.datetime.TimeZone? = null): String {
    if (this == null) return ""

    val timeZone = if (tz == null) {
        kotlinx.datetime.TimeZone.of(TimeZone.getDefault().id)
    } else {
        tz
    }
    val instant = Instant.fromEpochSeconds(this)
    return dateFormatter.format(instant.toLocalDateTime(timeZone).toJavaLocalDateTime())
}

fun TimeCardEventType?.eventTypeFriendlyName(): String {
    return when (this) {
        TimeCardEventType.CLOCK_IN -> "Entrada"
        TimeCardEventType.CLOCK_OUT -> "Salida"
        TimeCardEventType.OTHER -> "Otro"
        null -> "Desconocido"
    }
}

fun IdType.toIdTypeFriendlyName(): String {
    return when (this) {
        IdType.DNI -> "DNI"
        IdType.PASSPORT -> "Pasaporte"
        IdType.CE -> "CE"
        IdType.OTHER -> "Otro"
    }
}

fun EventType?.toFriendlyString(): String {
    return when (this) {
        EventType.GUEST -> "Invitado"
        EventType.DELIVERY -> "Cargo/Delivery"
        EventType.INCIDENT -> "Incidente/Seguridad"
        EventType.MAINTENANCE_SERVICE -> "Mantenimiento/Servicio"
        else -> "Otro"
    }
}