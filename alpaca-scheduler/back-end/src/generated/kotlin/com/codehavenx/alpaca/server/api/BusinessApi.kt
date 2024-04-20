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

fun Route.BusinessApi(json: Json) {
    post(Paths.addUserToBusiness) {
        call.respond(HttpStatusCode.NotImplemented)
    }

    post(Paths.CreateBusiness) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
          "name" : "Business Name"
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    delete(Paths.DeleteBusinessById) {
        call.respond(HttpStatusCode.NotImplemented)
    }

    get(Paths.GetBusinessById) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
          "name" : "Business Name"
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    get(Paths.GetBusinessesFromCriteria) {
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
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    delete(Paths.remoteUserFromBusiness) {
        call.respond(HttpStatusCode.NotImplemented)
    }

    put(Paths.UpdateBusinessById) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
          "name" : "Business Name"
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }
}
