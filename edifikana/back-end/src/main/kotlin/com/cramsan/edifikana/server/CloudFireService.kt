package com.cramsan.edifikana.server

import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.FormRecord
import com.cramsan.edifikana.lib.firestore.FormRecordPK
import com.cramsan.edifikana.lib.firestore.PropertyConfigPK
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
import com.cramsan.edifikana.lib.firestore.User
import com.cramsan.edifikana.lib.firestore.helpers.fullName
import com.cramsan.edifikana.lib.requireNotBlank
import com.cramsan.edifikana.lib.safeTimeZone
import com.cramsan.edifikana.server.drive.createFolder
import com.cramsan.edifikana.server.drive.createSpreadsheet
import com.cramsan.edifikana.server.drive.uploadFile
import com.cramsan.edifikana.server.firebase.getDocument
import com.cramsan.edifikana.server.firebase.getPropertyConfig
import com.cramsan.edifikana.server.firebase.updatePropertyConfig
import com.cramsan.edifikana.server.models.toEmployee
import com.cramsan.edifikana.server.models.toFirestoreEventLogRecord
import com.cramsan.edifikana.server.models.toFormRecord
import com.cramsan.edifikana.server.models.toRowEntry
import com.cramsan.edifikana.server.models.toTimeCardEvent
import com.cramsan.edifikana.server.sheets.appendValues
import com.cramsan.edifikana.server.sheets.checkIfSheetExists
import com.cramsan.edifikana.server.sheets.createSheetTab
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import com.cramsan.framework.thread.ThreadUtilInterface
import com.google.api.services.drive.Drive
import com.google.api.services.sheets.v4.Sheets
import com.google.cloud.firestore.Firestore
import com.google.events.cloud.firestore.v1.Document
import com.google.events.cloud.firestore.v1.DocumentEventData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.net.URL
import java.net.URLEncoder
import java.time.format.TextStyle
import java.util.Locale

class CloudFireService(
    private val projectName: String,
    private val sheets: Sheets,
    private val drive: Drive,
    private val firestore: Firestore,
    private val clock: Clock,
) : KoinComponent {

    companion object {
        private const val TAG = "CloudFireService"
    }

    @Suppress("UnusedPrivateProperty")
    private val eventLogger: EventLoggerInterface by inject()

    @Suppress("UnusedPrivateProperty")
    private val haltUtil: HaltUtil by inject()

    @Suppress("UnusedPrivateProperty")
    private val assertUtil: AssertUtilInterface by inject()

    @Suppress("UnusedPrivateProperty")
    private val threadUtil: ThreadUtilInterface by inject()

    private val dispatcherProvider: DispatcherProvider by inject()

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
    ) {
        logI(TAG, "Old value:")
        logI(TAG, documentEventData.oldValue.toString())

        logI(TAG, "New value:")
        logI(TAG, documentEventData.value.toString())

        logI(TAG, "Update value:")
        logI(TAG, documentEventData.updateMask.toString())

        val propertyConfig = getPropertyConfig(firestore, PropertyConfigPK("cenit_01"))
        // TODO: If there is an error, verify that the property config exists in the DB
        logI(TAG, "Property config loaded: $propertyConfig")

        val gDriveParams: GoogleDriveParameters = GoogleDriveParameters(
            storageFolderId = requireNotBlank(propertyConfig.storageFolderId),
            timeCardSpreadsheetId = requireNotBlank(propertyConfig.timeCardSpreadsheetId),
            eventLogSpreadsheetId = requireNotBlank(propertyConfig.eventLogSpreadsheetId),
            formEntriesSpreadsheetId = requireNotBlank(propertyConfig.formEntriesSpreadsheetId),
        )

        // An example of the documentEventData.value.name is:
        // projects/edifikana/databases/(default)/documents/timeCardRecords/DNI_47202201-CLOCK_OUT-1714891969
        val entryPath = documentEventData.value.name.split(firestoreEntryPrefix(projectName))[1]
        val entryParts = entryPath.split("/")
        val collection = entryParts[0]
        val documentId = entryParts[1]

        val currentLocalDateTime = clock.now().toLocalDateTime(safeTimeZone(propertyConfig.timeZone))
        val currentMonth = currentLocalDateTime.month.getDisplayName(
            TextStyle.FULL,
            Locale("es", "PE"),
        )

        logI(TAG, "Collection: $collection, Document ID: $documentId")
        when (collection) {
            Employee.COLLECTION -> {
                logW(TAG, "Employee collection found. Nothing to do.")
                return
            }
            TimeCardRecord.COLLECTION -> {
                processTimeCardRecord(EmployeePK(documentId), firestore, gDriveParams, currentMonth)
                return
            }
            EventLogRecord.COLLECTION -> {
                processEventLogRecord(
                    EventLogRecordPK(documentId),
                    firestore,
                    gDriveParams,
                    documentEventData.oldValue,
                    documentEventData.value,
                    currentMonth,
                )
                return
            }
            FormRecord.COLLECTION -> {
                processFormRecord(FormRecordPK(documentId), firestore, gDriveParams, currentMonth)
                return
            }
            User.COLLECTION -> {
                logW(TAG, "User collection found. Nothing to do.")
                return
            }
            else -> {
                logW(TAG, "Unknown collection: $collection")
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
        sheetName: String,
    ) {
        logI(TAG, "Processing time card record: $timeCardRecordPK")
        val document = getDocument(firestore, TimeCardRecord.COLLECTION, timeCardRecordPK.documentPath)

        val timeCardRecord = document.toTimeCardEvent()

        ensureSheetExists(sheets, gDriveParams.timeCardSpreadsheetId, sheetName)

        val employeeName = timeCardRecord.employeeDocumentId?.let {
            val employee = getDocument(firestore, Employee.COLLECTION, it).toEmployee()
            employee.fullName()
        }.orEmpty()

        val imageUrl = timeCardRecord.imageUrl?.let {
            "https://firebasestorage.googleapis.com/v0/b/$projectName.appspot.com/o/${urlEncode(it)}?alt=media"
        }.orEmpty()

        val driveImageUrl = if (imageUrl.isNotEmpty()) {
            logI(TAG, "TimecardRecordPk: $timeCardRecordPK - ImageUrl: $imageUrl")
            val url = URL(imageUrl)
            val imageData = url.readBytes()
            val imageFile = File.createTempFile("image", ".jpg")
            imageFile.writeBytes(imageData)

            uploadFile(
                drive,
                gDriveParams.storageFolderId,
                imageFile.absolutePath,
                "image/jpeg",
                timeCardRecordPK.documentPath,
            )
        } else {
            logW(TAG, "No image URL found")
            ""
        }

        appendValues(
            sheets,
            gDriveParams.timeCardSpreadsheetId,
            sheetName,
            listOf(
                timeCardRecord.toRowEntry(
                    employeeFullName = employeeName,
                    imageUrlOverride = driveImageUrl,
                ),
            ),
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
        newValue: Document,
        sheetName: String,
    ) {
        logI(TAG, "Processing event log record: $eventLogRecordPK")
        val oldEventLogRecord = oldValue.toFirestoreEventLogRecord()
        val eventLogRecord = newValue.toFirestoreEventLogRecord()

        ensureSheetExists(sheets, gDriveParams.eventLogSpreadsheetId, sheetName)

        val old = (oldEventLogRecord.attachments.orEmpty()).toSet()
        val new = (eventLogRecord.attachments.orEmpty()).toSet()
        val diff = new - old

        val employeeName = eventLogRecord.employeeDocumentId?.let {
            val employee = getDocument(firestore, Employee.COLLECTION, it).toEmployee()
            employee.fullName()
        }.orEmpty()

        val uploadedImages = diff.map { storageRef ->
            val imageUrl = storageRef.let {
                "https://firebasestorage.googleapis.com/v0/b/$projectName.appspot.com/o/${urlEncode(it)}?alt=media"
            }

            if (imageUrl.isNotEmpty()) {
                logI(TAG, "EventLogRecordPK: ${eventLogRecordPK.documentPath} - ImageUrl: $imageUrl")
                val url = URL(imageUrl)
                val imageData = url.readBytes()
                val imageFile = File.createTempFile("image", ".jpg")
                imageFile.writeBytes(imageData)

                uploadFile(
                    drive,
                    gDriveParams.storageFolderId,
                    imageFile.absolutePath,
                    "image/jpeg",
                    storageRef,
                )
            } else {
                logW(TAG, "No image URL found")
                ""
            }
        }.filter { it.isNotEmpty() }

        appendValues(
            sheets,
            gDriveParams.eventLogSpreadsheetId,
            sheetName,
            listOf(
                eventLogRecord.toRowEntry(employeeName, uploadedImages.joinToString("\n")),
            ),
        )
    }

    /**
     * Processes a form record.
     *
     * @param formRecordPK The form record primary key
     * @param firestore The Firestore instance
     * @param gDriveParams The Google Drive parameters
     */
    @OptIn(FireStoreModel::class)
    private fun processFormRecord(
        formRecordPK: FormRecordPK,
        firestore: Firestore,
        gDriveParams: GoogleDriveParameters,
        sheetName: String,
    ) {
        logI(TAG, "Processing form record: $formRecordPK")

        ensureSheetExists(sheets, gDriveParams.formEntriesSpreadsheetId, sheetName)

        val document = getDocument(firestore, FormRecord.COLLECTION, formRecordPK.documentPath)

        val formRecord = document.toFormRecord()

        appendValues(
            sheets,
            gDriveParams.formEntriesSpreadsheetId,
            sheetName,
            listOf(
                formRecord.toRowEntry(),
            ),
        )
    }

    @OptIn(FireStoreModel::class)
    suspend fun configureDrive(
        propertyConfigPK: PropertyConfigPK,
    ) {
        logI(TAG, "Configuring Google Drive to be able to run the service")

        val propertyConfig = getPropertyConfig(firestore, propertyConfigPK)
        // TODO: If there is an error, verify that the property config exists in the DB
        logI(TAG, "Property config loaded: $propertyConfig")

        val propertyFolderId = requireNotBlank(propertyConfig.driveFolderId)
        logI(TAG, "Property folder ID: $propertyFolderId")

        val storageFolderId = createFolder(
            drive,
            propertyFolderId,
            "Almacenamiento",
        )
        // TODO: If there is an error, that the folder is shared with the service account
        logI(TAG, "Storage folder ID: $storageFolderId")

        var timeCardSpreadsheetId: String? = null
        var eventLogSpreadsheetId: String? = null
        var formEntriesSpreadsheetId: String? = null
        coroutineScope {
            val timeCardJob = async(dispatcherProvider.ioDispatcher()) {
                timeCardSpreadsheetId = createSpreadsheet(
                    drive,
                    propertyFolderId,
                    "Hoja de tiempo",
                )
                logI(TAG, "Time card spreadsheet ID: $timeCardSpreadsheetId")
            }
            val eventLogJob = async(dispatcherProvider.ioDispatcher()) {
                eventLogSpreadsheetId = createSpreadsheet(
                    drive,
                    propertyFolderId,
                    "Registro de eventos",
                )
                logI(TAG, "Event log spreadsheet ID: $eventLogSpreadsheetId")
            }
            val formEntriesJob = async(dispatcherProvider.ioDispatcher()) {
                formEntriesSpreadsheetId = createSpreadsheet(
                    drive,
                    propertyFolderId,
                    "Entradas de formulario",
                )
                logI(TAG, "Form entries spreadsheet ID: $formEntriesSpreadsheetId")
            }
            awaitAll(timeCardJob, eventLogJob, formEntriesJob)
        }

        val updatedPropertyConfig = propertyConfig.copy(
            storageFolderId = storageFolderId,
            timeCardSpreadsheetId = timeCardSpreadsheetId,
            eventLogSpreadsheetId = eventLogSpreadsheetId,
            formEntriesSpreadsheetId = formEntriesSpreadsheetId,
        )
        logI(TAG, "Saving property config: $updatedPropertyConfig")

        updatePropertyConfig(firestore, updatedPropertyConfig)
        logI(TAG, "Property config updated")
    }

    private fun ensureSheetExists(
        sheets: Sheets,
        spreadsheetId: String,
        sheetName: String,
    ) {
        logI(TAG, "Ensuring spreadsheet $spreadsheetId has sheet $sheetName")
        if (!checkIfSheetExists(
                sheets,
                spreadsheetId,
                sheetName,
            )
        ) {
            createSheetTab(
                sheets,
                spreadsheetId,
                sheetName,
            )
        }
        logI(TAG, "Spreadsheet and sheet are ready")
    }
}

private fun urlEncode(path: String): String {
    return URLEncoder.encode(path, "UTF-8")
}

private const val DATABASE = "(default)"
private fun firestoreEntryPrefix(projectName: String) = "projects/$projectName/databases/$DATABASE/documents/"
