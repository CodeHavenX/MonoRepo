package com.cramsan.edifikana.client.android.utils

import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.firestore.IdType
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime

fun Employee.fullName() = "$name $lastName".trim()

// TODO: Move to resources
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

fun Long?.toFriendlyDateTime(): String {
    if (this == null) return ""

    val tz = kotlinx.datetime.TimeZone.of(TimeZone.getDefault().id)
    val instant = Instant.fromEpochSeconds(this)
    return dateFormatter.format(instant.toLocalDateTime(tz).toJavaLocalDateTime())
}

fun TimeCardEventType?.eventTypeFriendlyName(): String {
    return when (this) {
        TimeCardEventType.CLOCK_IN -> return "Entrada"
        TimeCardEventType.CLOCK_OUT -> return "Salida"
        null -> return "Desconocido"
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