package com.cramsan.edifikana.server

import com.cramsan.edifikana.server.firebase.getLocalFirebaseCredentials
import com.cramsan.edifikana.server.firebase.initializeFirebase
import com.cramsan.edifikana.server.firebase.initializeFirestoreService
import com.google.cloud.functions.CloudEventsFunction
import com.google.events.cloud.firestore.v1.DocumentEventData
import com.google.protobuf.InvalidProtocolBufferException
import io.cloudevents.CloudEvent
import java.util.Base64
import java.util.logging.Logger

class CloudFireController : CloudEventsFunction {
    companion object {
        private val logger: Logger = Logger.getLogger(CloudFireController::class.java.getName())
    }

    val credentials = getLocalFirebaseCredentials()

    val app = initializeFirebase(credentials)

    val firestore = initializeFirestoreService(app)

    @Throws(InvalidProtocolBufferException::class)
    override fun accept(event: CloudEvent) {
        logger.info("Function triggered by event on: " + event.source)
        logger.info("Event type: " + event.type)
        val payloadBytes = event.data!!.toBytes()
        val eventData = DocumentEventData.parseFrom(payloadBytes)

        val encodedString: String = Base64.getEncoder().encodeToString(payloadBytes)
        logger.warning("Encoded payload: $encodedString")

        val gDriveParams = GoogleDriveParameters(
            storageFolderId = System.getenv(STORAGE_FOLDER_ID_PARAM) ?: throw IllegalArgumentException(
                "Missing $STORAGE_FOLDER_ID_PARAM environment variable"
            ),
            timeCardSpreadsheetId = System.getenv(TIME_CARD_SPREADSHEET_ID_PARAM) ?: throw IllegalArgumentException(
                "Missing $TIME_CARD_SPREADSHEET_ID_PARAM environment variable"
            ),
            eventLogSpreadsheetId = System.getenv(EVENT_LOG_SPREADSHEET_ID_PARAM) ?: throw IllegalArgumentException(
                "Missing $EVENT_LOG_SPREADSHEET_ID_PARAM environment variable"
            ),
        )

        CloudFireService().processEvent(eventData, firestore, gDriveParams)
        logger.info("Invocation complete")
    }
}

private const val STORAGE_FOLDER_ID_PARAM = "STORAGE_FOLDER_ID"
private const val TIME_CARD_SPREADSHEET_ID_PARAM = "TIME_CARD_SPREADSHEET_ID"
private const val EVENT_LOG_SPREADSHEET_ID_PARAM = "EVENT_LOG_SPREADSHEET_ID"

fun main() {
    val payload = "CosDCmJwcm9qZWN0cy9lZGlmaWthbmEvZGF0YWJhc2VzLyhkZWZhdWx0KS9kb2N1bWVudHMvdGltZUNhcmRSZWNvcmRz" +
        "L0ROSV80NzIwMjIwMS1DTE9DS19PVVQtMTcxNDg5MTk2ORIcChZmYWxsYmFja0VtcGxveWVlSWRUeXBlEgJYABIeChhmYWxsYmF" +
        "ja0VtcGxveWVlSWRSZWFzb24SAlgAEjUKCGltYWdlVXJsEimKASZjbG9ja2lub3V0LzIwMjQtMDUtMDQtMjMtNTItNDctODQ2Lmp" +
        "wZxITCglldmVudFRpbWUSBhDB2dyxBhIhChtmYWxsYmFja0VtcGxveWVlSWRUeXBlT3RoZXISAlgAEhkKCWV2ZW50VHlwZRIMigE" +
        "JQ0xPQ0tfT1VUEhoKFGZhbGxiYWNrRW1wbG95ZWVOYW1lEgJYABIlChJlbXBsb3llZURvY3VtZW50SWQSD4oBDEROSV80NzIwMj" +
        "IwMRoMCMTZ3LEGEJjP06UDIgwIxNncsQYQmM/TpQM="
    val data = Base64.getDecoder().decode(payload)
    val firestoreEventData: DocumentEventData = DocumentEventData.parseFrom(data)

    val firestore = CloudFireController().firestore

    val gDriveParams = GoogleDriveParameters(
        storageFolderId = "1ZRI7dP2X7VqwixGKz3OPJ6KB-uuDkkM1",
        timeCardSpreadsheetId = "1mDgyQtJV_EkCrikM5-lBufwmFGS5N8FxMUVYvdbXSuM",
        eventLogSpreadsheetId = "1v-we-o55vEu8tGItH0EvY0v4IJIwAPtW75ZbdPPNyQA",
    )
    CloudFireService().processEvent(firestoreEventData, firestore, gDriveParams)
}
