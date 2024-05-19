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

class CloudFirebaseApp : CloudEventsFunction {
    companion object {
        private val logger: Logger = Logger.getLogger(CloudFirebaseApp::class.java.getName())
    }

    val credentials = getLocalFirebaseCredentials()

    val app = initializeFirebase(credentials)

    val firestore = initializeFirestoreService(app)

    @Throws(InvalidProtocolBufferException::class)
    override fun accept(event: CloudEvent) {
        logger.info("Function triggered by event on: " + event.source)
        logger.info("Event type: " + event.type)
        val payloadBytes = event.data!!.toBytes()

        val encodedString: String = Base64.getEncoder().encodeToString(payloadBytes)
        logger.warning("Encoded payload: $encodedString")

        val projectName = System.getenv(PROJECT_NAME) ?: throw IllegalArgumentException(
            "Missing $STORAGE_FOLDER_ID_PARAM environment variable"
        )

        val eventData = DocumentEventData.parseFrom(payloadBytes)
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
            formEntriesSpreadsheetId = System.getenv(
                FORM_ENTRIES_SPREADSHEET_ID_PARAM
            ) ?: throw IllegalArgumentException(
                "Missing $FORM_ENTRIES_SPREADSHEET_ID_PARAM environment variable"
            ),
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

fun main() {
    val payload = "CuYFCmVwcm9qZWN0cy9lZGlmaWthbmEvZGF0YWJhc2VzLyhkZWZhdWx0KS9kb2N1bWVudHMvZXZlbnRM" +
        "b2cvRE5JXzQ3NTI0ODExLTE3MTUxODk2MTktTUFJTlRFTkFOQ0VfU0VSVklDRRIlCgdzdW1tYXJ5EhqKARdzZX" +
        "J2aWNpbyBkZSBqYXJkaW5lcsOtYRIYChFmYWxsYmFja0V2ZW50VHlwZRIDigEAEg4KBHVuaXQSBooBAzEwORIWCgx" +
        "0aW1lUmVjb3JkZWQSBhDz7u6xBhKOAwoLZGVzY3JpcHRpb24S/gKKAfoCaW5ncmVzYXJvbiBwZXJzb25hbCBwYX" +
        "JhIHNlcnZpY2lvIGRlIGphcmRpbmVyw61hIEZlcm5hbmRvIEVucmlxdWUgUGxhbm8gRE5JIDE2NTgyNTQsIGxh" +
        "IHNydGEuIGFkbWluaXN0cmFkb3JhIGxlcyBtb3N0csOzIGRldGFsbGFkYW1lbnRlIGxvcyBsdWdhcmVzIHF1ZS" +
        "BzZSBkZWJlIHJlYWxpemFyIGxvcyB0cmFiYWpvcy4gRWwgc3IuIGphcmRpbmVybyBpbmRpY8OzIHF1ZSBoYXkg" +
        "cGxhbnRhcyBlbmZlcm1hcyB5IHF1ZSBuZWNlc2l0YW4gZnVtaWdhci4gYSBwYXJ0ZSBkZSBsYSBzcnRhLiBhZG1" +
        "pbmlzdHJhZG9yYSBsYSBzcmEuIENsYXVkaWEgQm9uYW5pIHRhbWJpw6luIGVzdMOhIGVuc2XDsWFuZG8gYWwgamF" +
        "yZGluZXJvIGxvcyBsdWdhcmVzIGEgdHJhYmFqYXIuEiMKCWV2ZW50VHlwZRIWigETTUFJTlRFTkFOQ0VfU0VSV" +
        "klDRRIbChRmYWxsYmFja0VtcGxveWVlTmFtZRIDigEAEiUKEmVtcGxveWVlRG9jdW1lbnRJZBIPigEMRE5JXzQ3" +
        "NTI0ODExGgwI9+7usQYQmMXusQEiDAj37u6xBhCYxe6xAQ=="
    val data = Base64.getDecoder().decode(payload)
    val firestoreEventData: DocumentEventData = DocumentEventData.parseFrom(data)

    val firestore = CloudFirebaseApp().firestore

    val gDriveParams = GoogleDriveParameters(
        storageFolderId = "1ZRI7dP2X7VqwixGKz3OPJ6KB-uuDkkM1",
        timeCardSpreadsheetId = "1mDgyQtJV_EkCrikM5-lBufwmFGS5N8FxMUVYvdbXSuM",
        eventLogSpreadsheetId = "1v-we-o55vEu8tGItH0EvY0v4IJIwAPtW75ZbdPPNyQA",
        formEntriesSpreadsheetId = "16Fqq_uC6fyQVEhn7wTbNVcCjFLWuvgS676ic2ALrOzk",
    )
    CloudFireService("edifikana-stage").processEvent(firestoreEventData, firestore, gDriveParams)
}
