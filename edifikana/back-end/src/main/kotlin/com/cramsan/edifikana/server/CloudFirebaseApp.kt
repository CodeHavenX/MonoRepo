package com.cramsan.edifikana.server

import com.cramsan.edifikana.server.firebase.getLocalFirebaseCredentials
import com.cramsan.edifikana.server.firebase.initializeFirebase
import com.cramsan.edifikana.server.firebase.initializeFirestoreService
import com.cramsan.edifikana.server.json.jsonFormat
import com.google.cloud.functions.CloudEventsFunction
import com.google.events.cloud.firestore.v1.DocumentEventData
import com.google.protobuf.InvalidProtocolBufferException
import io.cloudevents.CloudEvent
import java.util.Base64
import java.util.logging.Logger

class CloudFirebaseApp : CloudEventsFunction {
    companion object {
        private val logger: Logger = Logger.getLogger(CloudFirebaseApp::class.java.getName())
    }

    private val credentials = getLocalFirebaseCredentials()

    private val app = initializeFirebase(credentials)

    private val firestore = initializeFirestoreService(app)

    /**
     * These are public so we can overwrite them when running locally
     */
    var projectName = System.getenv(PROJECT_NAME)

    var storageFolderId = System.getenv(STORAGE_FOLDER_ID_PARAM)

    var timeCardSpreadsheetId = System.getenv(TIME_CARD_SPREADSHEET_ID_PARAM)

    var eventLogSpreadsheetId = System.getenv(EVENT_LOG_SPREADSHEET_ID_PARAM)

    var formEntriesSpreadsheetId = System.getenv(FORM_ENTRIES_SPREADSHEET_ID_PARAM)

    @Throws(InvalidProtocolBufferException::class)
    override fun accept(event: CloudEvent) {
        logger.info("Function triggered by event on: " + event.source)
        logger.info("Event type: " + event.type)

        val payloadBytes = jsonFormat.serialize(event)
        val encodedString: String = Base64.getEncoder().encodeToString(payloadBytes)
        logger.warning("Encoded event: $encodedString")

        require(projectName.isNotBlank()) { "Missing $PROJECT_NAME environment variable" }
        require(storageFolderId.isNotBlank()) { "Missing $STORAGE_FOLDER_ID_PARAM environment variable" }
        require(timeCardSpreadsheetId.isNotBlank()) { "Missing $TIME_CARD_SPREADSHEET_ID_PARAM environment variable" }
        require(eventLogSpreadsheetId.isNotBlank()) { "Missing $EVENT_LOG_SPREADSHEET_ID_PARAM environment variable" }
        require(formEntriesSpreadsheetId.isNotBlank()) {
            "Missing $FORM_ENTRIES_SPREADSHEET_ID_PARAM environment variable"
        }

        val eventData = DocumentEventData.parseFrom(event.data!!.toBytes())
        val gDriveParams = GoogleDriveParameters(
            storageFolderId = storageFolderId,
            timeCardSpreadsheetId = timeCardSpreadsheetId,
            eventLogSpreadsheetId = eventLogSpreadsheetId,
            formEntriesSpreadsheetId = formEntriesSpreadsheetId,
        )

        CloudFireService(projectName).processEvent(eventData, firestore, gDriveParams)
        logger.info("Invocation complete")
    }
}

private const val PROJECT_NAME = "PROJECT_NAME"
private const val STORAGE_FOLDER_ID_PARAM = "STORAGE_FOLDER_ID"
private const val TIME_CARD_SPREADSHEET_ID_PARAM = "TIME_CARD_SPREADSHEET_ID"
private const val EVENT_LOG_SPREADSHEET_ID_PARAM = "EVENT_LOG_SPREADSHEET_ID"
private const val FORM_ENTRIES_SPREADSHEET_ID_PARAM = "FORM_ENTRIES_SPREADSHEET_ID"
