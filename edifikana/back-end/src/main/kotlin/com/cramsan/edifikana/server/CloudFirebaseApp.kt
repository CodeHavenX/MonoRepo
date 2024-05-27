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

    var launchParametersProvider: () -> FunctionLaunchParameters = { FunctionLaunchParameters.fromSystemEnvironment() }

    var dependencies: FunctionDependencies = DependenciesLocalCredentials()

    @Throws(InvalidProtocolBufferException::class)
    override fun accept(event: CloudEvent) {
        logger.info("Function triggered by event on: " + event.source)
        logger.info("Event type: " + event.type)

        val payloadBytes = jsonFormat.serialize(event)
        val encodedString: String = Base64.getEncoder().encodeToString(payloadBytes)
        logger.warning("Encoded event: $encodedString")

        val functionLaunchParameters = launchParametersProvider()

        val eventData = DocumentEventData.parseFrom(event.data!!.toBytes())
        val gDriveParams = GoogleDriveParameters(
            storageFolderId = functionLaunchParameters.storageFolderId,
            timeCardSpreadsheetId = functionLaunchParameters.timeCardSpreadsheetId,
            eventLogSpreadsheetId = functionLaunchParameters.eventLogSpreadsheetId,
            formEntriesSpreadsheetId = functionLaunchParameters.formEntriesSpreadsheetId,
        )

        CloudFireService(
            functionLaunchParameters.projectName,
            dependencies.sheets,
            dependencies.drive,
            dependencies.firestore,
        ).processEvent(eventData, gDriveParams)
        logger.info("Invocation complete")
    }
}