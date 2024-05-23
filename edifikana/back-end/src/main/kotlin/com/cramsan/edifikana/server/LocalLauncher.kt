package com.cramsan.edifikana.server

import com.cramsan.edifikana.server.json.jsonFormat
import com.google.firebase.FirebaseApp
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import java.util.Base64

fun main() {
    embeddedServer(Netty, port = 8954) {
        routing {
            post("/") {
                val payload = call.receiveText().trim()

                val projectName = call.parameters.getOrFail("projectName")
                val storageFolderId = call.parameters.getOrFail("storageFolderId")
                val timeCardSpreadsheetId = call.parameters.getOrFail("timeCardSpreadsheetId")
                val eventLogSpreadsheetId = call.parameters.getOrFail("eventLogSpreadsheetId")
                val formEntriesSpreadsheetId = call.parameters.getOrFail("formEntriesSpreadsheetId")

                try {
                    val firebaseApp = CloudFirebaseApp()
                    firebaseApp.projectName = projectName
                    firebaseApp.storageFolderId = storageFolderId
                    firebaseApp.timeCardSpreadsheetId = timeCardSpreadsheetId
                    firebaseApp.eventLogSpreadsheetId = eventLogSpreadsheetId
                    firebaseApp.formEntriesSpreadsheetId = formEntriesSpreadsheetId

                    val decodedString = Base64.getDecoder().decode(payload)
                    val event = jsonFormat.deserialize(decodedString)
                    firebaseApp.accept(event)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    call.respond("ERROR")
                    return@post
                } finally {
                    FirebaseApp.getApps().forEach { it.delete() }
                }

                call.respond("OK")
            }
        }
    }.start(wait = true)
}
