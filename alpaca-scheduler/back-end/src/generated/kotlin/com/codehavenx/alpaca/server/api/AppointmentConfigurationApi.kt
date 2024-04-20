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

fun Route.AppointmentConfigurationApi(json: Json) {
    post(Paths.CreateAppointmentConfiguration) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "duration" : 30,
          "appointmentType" : {
            "appointmentType" : "Consultation"
          },
          "timezone" : "Europe/Berlin",
          "appointmentConfigurationId" : "51083ddd-4adb-4595-8103-ad6852164311"
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.parseToJsonElement(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    delete(Paths.DeleteAppointmentConfigurationById) {
        call.respond(HttpStatusCode.NotImplemented)
    }

    get(Paths.GetAppointmentConfigurationById) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "duration" : 30,
          "appointmentType" : {
            "appointmentType" : "Consultation"
          },
          "timezone" : "Europe/Berlin",
          "appointmentConfigurationId" : "51083ddd-4adb-4595-8103-ad6852164311"
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.parseToJsonElement(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    get(Paths.GetAppointmentConfigurationsForBusiness) {
        val exampleContentType = "application/json"
        val exampleContentString = """[ {
          "duration" : 30,
          "appointmentType" : {
            "appointmentType" : "Consultation"
          },
          "timezone" : "Europe/Berlin",
          "appointmentConfigurationId" : "51083ddd-4adb-4595-8103-ad6852164311"
        }, {
          "duration" : 30,
          "appointmentType" : {
            "appointmentType" : "Consultation"
          },
          "timezone" : "Europe/Berlin",
          "appointmentConfigurationId" : "51083ddd-4adb-4595-8103-ad6852164311"
        } ]"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.parseToJsonElement(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    put(Paths.UpdateAppointmentConfigurationById) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "duration" : 30,
          "appointmentType" : {
            "appointmentType" : "Consultation"
          },
          "timezone" : "Europe/Berlin",
          "appointmentConfigurationId" : "51083ddd-4adb-4595-8103-ad6852164311"
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.parseToJsonElement(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }
}
