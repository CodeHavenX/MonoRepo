package com.codehavenx.alpaca.server.api

import com.codehavenx.alpaca.server.Paths
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.json.Json

fun Route.AvailabilityApi(json: Json) {

    get(Paths.GetAvailability) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "calendar" : [ {
            "date" : "2024-05-13",
            "startTime" : "1020",
            "endTime" : "1020",
            "events" : [ {
              "calendarEventId" : "187ff571-61a7-4070-b5ee-32c97869c2a8",
              "startDateTime" : "2024-04-20T09:30:00Z",
              "participantsId" : [ "61058763-4faa-4174-97fb-1df89dfcb466", "61058763-4faa-4174-97fb-1df89dfcb466" ],
              "title" : "Appointment with John Doe",
              "endDateTime" : "2024-04-20T09:30:00Z",
              "ownerId" : "61058763-4faa-4174-97fb-1df89dfcb466"
            }, {
              "calendarEventId" : "187ff571-61a7-4070-b5ee-32c97869c2a8",
              "startDateTime" : "2024-04-20T09:30:00Z",
              "participantsId" : [ "61058763-4faa-4174-97fb-1df89dfcb466", "61058763-4faa-4174-97fb-1df89dfcb466" ],
              "title" : "Appointment with John Doe",
              "endDateTime" : "2024-04-20T09:30:00Z",
              "ownerId" : "61058763-4faa-4174-97fb-1df89dfcb466"
            } ]
          }, {
            "date" : "2024-05-13",
            "startTime" : "1020",
            "endTime" : "1020",
            "events" : [ {
              "calendarEventId" : "187ff571-61a7-4070-b5ee-32c97869c2a8",
              "startDateTime" : "2024-04-20T09:30:00Z",
              "participantsId" : [ "61058763-4faa-4174-97fb-1df89dfcb466", "61058763-4faa-4174-97fb-1df89dfcb466" ],
              "title" : "Appointment with John Doe",
              "endDateTime" : "2024-04-20T09:30:00Z",
              "ownerId" : "61058763-4faa-4174-97fb-1df89dfcb466"
            }, {
              "calendarEventId" : "187ff571-61a7-4070-b5ee-32c97869c2a8",
              "startDateTime" : "2024-04-20T09:30:00Z",
              "participantsId" : [ "61058763-4faa-4174-97fb-1df89dfcb466", "61058763-4faa-4174-97fb-1df89dfcb466" ],
              "title" : "Appointment with John Doe",
              "endDateTime" : "2024-04-20T09:30:00Z",
              "ownerId" : "61058763-4faa-4174-97fb-1df89dfcb466"
            } ]
          } ],
          "endDate" : "2024-05-13",
          "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466",
          "startDate" : "2024-05-13"
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            "application/xml" -> call.respondText(exampleContentString, ContentType.Text.Xml)
            else -> call.respondText(exampleContentString)
        }
    }

    get(Paths.GetAvailabilityTimeSlots) {
        val exampleContentType = "application/json"
        val exampleContentString = """[ {
          "startTime" : "2024-04-20T09:30:00Z",
          "endTime" : "2024-04-20T09:30:00Z",
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
        }, {
          "startTime" : "2024-04-20T09:30:00Z",
          "endTime" : "2024-04-20T09:30:00Z",
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
        } ]"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            "application/xml" -> call.respondText(exampleContentString, ContentType.Text.Xml)
            else -> call.respondText(exampleContentString)
        }
    }

    post(Paths.setAvailability) {
        call.respond(HttpStatusCode.NotImplemented)
    }

    post(Paths.setAvailabilityOverride) {
        call.respond(HttpStatusCode.NotImplemented)
    }
}
