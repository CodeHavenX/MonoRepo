package com.codehavenx.alpaca.server.api

import com.codehavenx.alpaca.server.Paths
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import kotlinx.serialization.json.Json

fun Route.AppointmentApi(json: Json) {

    post(Paths.CreateAppointment) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "appointmentId" : "b4fe2a16-5ce4-46e3-b522-12f0549d9880",
          "timeSlot" : {
            "startTime" : "2024-04-20T09:30:00Z",
            "endTime" : "2024-04-20T09:30:00Z",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          },
          "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466",
          "appointmentConfiguration" : {
            "duration" : 30,
            "appointmentType" : {
              "appointmentType" : "Consultation"
            },
            "timezone" : "Europe/Berlin",
            "appointmentConfigurationId" : "51083ddd-4adb-4595-8103-ad6852164311"
          }
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    delete(Paths.DeleteAppointmentById) {
        call.respond(HttpStatusCode.NotImplemented)
    }

    get(Paths.GetAppointmentById) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "appointmentId" : "b4fe2a16-5ce4-46e3-b522-12f0549d9880",
          "timeSlot" : {
            "startTime" : "2024-04-20T09:30:00Z",
            "endTime" : "2024-04-20T09:30:00Z",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          },
          "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466",
          "appointmentConfiguration" : {
            "duration" : 30,
            "appointmentType" : {
              "appointmentType" : "Consultation"
            },
            "timezone" : "Europe/Berlin",
            "appointmentConfigurationId" : "51083ddd-4adb-4595-8103-ad6852164311"
          }
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    get(Paths.GetAppointmentsFromCriteria) {
        val exampleContentType = "application/json"
        val exampleContentString = """[ {
          "appointmentId" : "b4fe2a16-5ce4-46e3-b522-12f0549d9880",
          "timeSlot" : {
            "startTime" : "2024-04-20T09:30:00Z",
            "endTime" : "2024-04-20T09:30:00Z",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          },
          "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466",
          "appointmentConfiguration" : {
            "duration" : 30,
            "appointmentType" : {
              "appointmentType" : "Consultation"
            },
            "timezone" : "Europe/Berlin",
            "appointmentConfigurationId" : "51083ddd-4adb-4595-8103-ad6852164311"
          }
        }, {
          "appointmentId" : "b4fe2a16-5ce4-46e3-b522-12f0549d9880",
          "timeSlot" : {
            "startTime" : "2024-04-20T09:30:00Z",
            "endTime" : "2024-04-20T09:30:00Z",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          },
          "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466",
          "appointmentConfiguration" : {
            "duration" : 30,
            "appointmentType" : {
              "appointmentType" : "Consultation"
            },
            "timezone" : "Europe/Berlin",
            "appointmentConfigurationId" : "51083ddd-4adb-4595-8103-ad6852164311"
          }
        } ]"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    put(Paths.UpdateAppointmentById) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "appointmentId" : "b4fe2a16-5ce4-46e3-b522-12f0549d9880",
          "timeSlot" : {
            "startTime" : "2024-04-20T09:30:00Z",
            "endTime" : "2024-04-20T09:30:00Z",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          },
          "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466",
          "appointmentConfiguration" : {
            "duration" : 30,
            "appointmentType" : {
              "appointmentType" : "Consultation"
            },
            "timezone" : "Europe/Berlin",
            "appointmentConfigurationId" : "51083ddd-4adb-4595-8103-ad6852164311"
          }
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }
}
