package com.codehavenx.alpaca.server.api

import com.codehavenx.alpaca.server.Paths
import com.codehavenx.alpaca.server.model.UserRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import kotlinx.serialization.json.Json

fun Route.UserApi(json: Json) {
    post(Paths.CreateUser) {
        val createUserRequest = call.receive<UserRequest>()

        val exampleContentType = "application/json"
        val exampleContentString = """{
          "name" : "John Doe",
          "relations" : [ {
            "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          }, {
            "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          } ],
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(HttpStatusCode.Created, json.parseToJsonElement(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    delete(Paths.DeleteUserById) {
        call.respond(HttpStatusCode.NotImplemented)
    }

    get(Paths.GetUserById) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "name" : "John Doe",
          "relations" : [ {
            "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          }, {
            "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          } ],
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    get(Paths.GetUsersFromCriteria) {
        val exampleContentType = "application/json"
        val exampleContentString = """[ {
          "name" : "John Doe",
          "relations" : [ {
            "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          }, {
            "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          } ],
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
        }, {
          "name" : "John Doe",
          "relations" : [ {
            "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          }, {
            "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          } ],
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
        } ]"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }

    put(Paths.UpdateUserById) {
        val exampleContentType = "application/json"
        val exampleContentString = """{
          "name" : "John Doe",
          "relations" : [ {
            "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          }, {
            "businessId" : "06a9f74a-1850-46ef-9573-9b6afb46da90",
            "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
          } ],
          "userId" : "61058763-4faa-4174-97fb-1df89dfcb466"
        }"""

        when (exampleContentType) {
            "application/json" -> call.respond(json.decodeFromString(exampleContentString))
            else -> call.respondText(exampleContentString)
        }
    }
}
