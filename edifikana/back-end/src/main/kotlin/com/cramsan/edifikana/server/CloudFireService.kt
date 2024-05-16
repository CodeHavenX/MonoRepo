package com.cramsan.edifikana.server

import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.EventType
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
import com.cramsan.edifikana.lib.firestore.User
import com.cramsan.edifikana.lib.firestore.helpers.fullName
import com.cramsan.edifikana.server.drive.appendValues
import com.cramsan.edifikana.server.drive.getHttpTransport
import com.cramsan.edifikana.server.drive.getLocalDriveCredentials
import com.cramsan.edifikana.server.drive.getRequestInitializer
import com.cramsan.edifikana.server.drive.initializeDriveService
import com.cramsan.edifikana.server.drive.initializeSpreadsheetService
import com.cramsan.edifikana.server.drive.uploadFile
import com.cramsan.edifikana.server.firebase.getDocument
import com.cramsan.edifikana.server.models.toEmployee
import com.cramsan.edifikana.server.models.toRowEntry
import com.cramsan.edifikana.server.models.toTimeCardEvent
import com.google.api.services.drive.Drive
import com.google.api.services.sheets.v4.Sheets
import com.google.cloud.firestore.Firestore
import com.google.events.cloud.firestore.v1.Document
import com.google.events.cloud.firestore.v1.DocumentEventData
import java.io.File
import java.net.URL
import java.net.URLEncoder
import java.util.logging.Logger

class CloudFireService {
    companion object {
        private val logger: Logger = Logger.getLogger(CloudFireService::class.java.getName())
    }

    private val sheets: Sheets

    private val drive: Drive

    init {
        val credentials = getLocalDriveCredentials()
        val httpTransport = getHttpTransport()
        val requestInitializer = getRequestInitializer(credentials)

        sheets = initializeSpreadsheetService(httpTransport, requestInitializer)
        drive = initializeDriveService(httpTransport, requestInitializer)
    }

    /**
     * Processes the event data. This function is called by the CloudFireController. It processes the event data based
     * on the content of the [documentEventData].
     *
     * @param documentEventData The document event data
     * @param firestore The Firestore instance
     * @param gDriveParams The Google Drive parameters
     */
    @OptIn(FireStoreModel::class)
    fun processEvent(
        documentEventData: DocumentEventData,
        firestore: Firestore,
        gDriveParams: GoogleDriveParameters,
    ) {
        logger.info("New value:")
        logger.info(documentEventData.value.toString())

        // An example of the documentEventData.value.name is:
        // projects/edifikana/databases/(default)/documents/timeCardRecords/DNI_47202201-CLOCK_OUT-1714891969
        val entryPath = documentEventData.value.name.replace(FIRESTORE_ENTRY_PREFIX, "")
        val entryParts = entryPath.split("/")
        val collection = entryParts[0]
        val documentId = entryParts[1]

        logger.info("Collection: $collection, Document ID: $documentId")
        when (collection) {
            Employee.COLLECTION -> {
                logger.warning("Employee collection found. Nothing to do.")
                return
            }
            TimeCardRecord.COLLECTION -> {
                processTimeCardRecord(EmployeePK(documentId), firestore, gDriveParams)
                return
            }
            EventLogRecord.COLLECTION -> {
                processEventLogRecord(
                    EventLogRecordPK(documentId),
                    firestore,
                    gDriveParams,
                    documentEventData.oldValue,
                    documentEventData.value,
                )
                return
            }
            User.COLLECTION -> {
                logger.warning("User collection found. Nothing to do.")
                return
            }
            else -> {
                logger.warning("Unknown collection: $collection")
                return
            }
        }
    }

    /**
     * Processes a time card record.
     *
     * @param timeCardRecordPK The time card record primary key
     * @param firestore The Firestore instance
     * @param gDriveParams The Google Drive parameters
     */
    @OptIn(FireStoreModel::class)
    private fun processTimeCardRecord(
        timeCardRecordPK: EmployeePK,
        firestore: Firestore,
        gDriveParams: GoogleDriveParameters,
    ) {
        logger.info("Processing time card record: $timeCardRecordPK")
        val document = getDocument(firestore, TimeCardRecord.COLLECTION, timeCardRecordPK.documentPath)

        val timeCardRecord = document.toTimeCardEvent()

        val employeeName = timeCardRecord.employeeDocumentId?.let {
            val employee = getDocument(firestore, Employee.COLLECTION, it).toEmployee()
            employee.fullName()
        }.orEmpty()

        val imageUrl = timeCardRecord.imageUrl?.let {
            "https://firebasestorage.googleapis.com/v0/b/edifikana.appspot.com/o/${urlEncode(it)}?alt=media"
        }.orEmpty()

        val driveImageUrl = if (imageUrl.isNotEmpty()) {
            logger.info("TimecardRecordPk: $timeCardRecordPK - ImageUrl: $imageUrl")
            val url = URL(imageUrl)
            val imageData = url.readBytes()
            val imageFile = File.createTempFile("image", ".jpg")
            imageFile.writeBytes(imageData)

            uploadFile(
                drive,
                gDriveParams.storageFolderId,
                imageFile.absolutePath,
                "image/jpeg",
                timeCardRecordPK.documentPath
            )
        } else {
            logger.warning("No image URL found")
            ""
        }

        appendValues(
            sheets,
            gDriveParams.timeCardSpreadsheetId,
            "Hoja 1",
            listOf(
                timeCardRecord.toRowEntry(
                    employeeFullName = employeeName,
                    imageUrlOverride = driveImageUrl,
                )
            )
        )
    }

    /**
     * Processes an event log record.
     *
     * @param eventLogRecordPK The event log record primary key
     * @param firestore The Firestore instance
     * @param gDriveParams The Google Drive parameters
     */
    @OptIn(FireStoreModel::class)
    private fun processEventLogRecord(
        eventLogRecordPK: EventLogRecordPK,
        firestore: Firestore,
        gDriveParams: GoogleDriveParameters,
        oldValue: Document,
        value1: Document,
    ) {
        logger.info("Processing event log record: $eventLogRecordPK")
        val oldEventLogRecord = oldValue.toObject()
        val eventLogRecord = value1.toObject()

        val old = (oldEventLogRecord.attachments ?: listOf()).toSet()
        val new = (eventLogRecord.attachments ?: listOf()).toSet()
        val diff = new - old

        val employeeName = eventLogRecord.employeeDocumentId?.let {
            val employee = getDocument(firestore, Employee.COLLECTION, it).toEmployee()
            employee.fullName()
        }.orEmpty()

        val uploadedImages = diff.map {
            val imageUrl = it.let {
                "https://firebasestorage.googleapis.com/v0/b/edifikana.appspot.com/o/${urlEncode(it)}?alt=media"
            }

            if (imageUrl.isNotEmpty()) {
                logger.info("EventLogRecordPK: ${eventLogRecordPK.documentPath} - ImageUrl: $imageUrl")
                val url = URL(imageUrl)
                val imageData = url.readBytes()
                val imageFile = File.createTempFile("image", ".jpg")
                imageFile.writeBytes(imageData)

                uploadFile(
                    drive,
                    gDriveParams.storageFolderId,
                    imageFile.absolutePath,
                    "image/jpeg",
                    it,
                )
            } else {
                logger.warning("No image URL found")
                ""
            }
        }.filter { it.isNotEmpty() }

        appendValues(
            sheets,
            gDriveParams.eventLogSpreadsheetId,
            "Hoja 1",
            listOf(
                eventLogRecord.toRowEntry(employeeName, uploadedImages.joinToString("\n"))
            )
        )
    }
}

@FireStoreModel
fun Document.toObject(): EventLogRecord {
    return EventLogRecord(
        employeeDocumentId = this.getFieldsOrDefault("employeeDocumentId", null)?.stringValue,
        timeRecorded = this.getFieldsOrDefault("timeRecorded", null)?.integerValue,
        unit = this.getFieldsOrDefault("unit", null)?.stringValue,
        eventType = this.getFieldsOrDefault("eventType", null)?.let {
            EventType.fromString(it.stringValue)
        } ?: EventType.OTHER,
        fallbackEmployeeName = this.getFieldsOrDefault("fallbackEmployeeName", null)?.stringValue,
        fallbackEventType = this.getFieldsOrDefault("fallbackEventType", null)?.stringValue,
        summary = this.getFieldsOrDefault("summary", null)?.stringValue,
        description = this.getFieldsOrDefault("description", null)?.stringValue,
        attachments = this.getFieldsOrDefault("attachments", null)?.arrayValue?.valuesList?.map {
            it.stringValue
        },

    )
}

private fun urlEncode(path: String): String {
    return URLEncoder.encode(path, "UTF-8")
}

private val PROJECT_NAME = "edifikana"
private val DATABASE = "(default)"
private val FIRESTORE_ENTRY_PREFIX = "projects/$PROJECT_NAME/databases/$DATABASE/documents/"
