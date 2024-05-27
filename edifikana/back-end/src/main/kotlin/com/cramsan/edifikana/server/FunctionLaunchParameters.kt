package com.cramsan.edifikana.server

class FunctionLaunchParameters(
    val projectName: String,
    val storageFolderId: String,
    val timeCardSpreadsheetId: String,
    val eventLogSpreadsheetId: String,
    val formEntriesSpreadsheetId: String,
) {

    init {
        require(projectName.isNotBlank()) {
            "Variable projectName variable"
        }
        require(storageFolderId.isNotBlank()) {
            "Missing storageFolderId variable"
        }
        require(timeCardSpreadsheetId.isNotBlank()) {
            "Missing timeCardSpreadsheetId variable"
        }
        require(eventLogSpreadsheetId.isNotBlank()) {
            "Missing eventLogSpreadsheetId variable"
        }
        require(formEntriesSpreadsheetId.isNotBlank()) {
            "Missing formEntriesSpreadsheetId variable"
        }
    }

    companion object {
        fun fromSystemEnvironment(): FunctionLaunchParameters {
            val loadedProjectName = System.getenv(PROJECT_NAME)
            val loadedStorageFolderId = System.getenv(STORAGE_FOLDER_ID_PARAM)
            val loadedTimeCardSpreadsheetId = System.getenv(TIME_CARD_SPREADSHEET_ID_PARAM)
            val loadedEventLogSpreadsheetId = System.getenv(EVENT_LOG_SPREADSHEET_ID_PARAM)
            val loadedFormEntriesSpreadsheetId = System.getenv(FORM_ENTRIES_SPREADSHEET_ID_PARAM)

            require(loadedProjectName.isNotBlank()) {
                "Missing $PROJECT_NAME environment variable"
            }
            require(loadedStorageFolderId.isNotBlank()) {
                "Missing $STORAGE_FOLDER_ID_PARAM environment variable"
            }
            require(loadedTimeCardSpreadsheetId.isNotBlank()) {
                "Missing $TIME_CARD_SPREADSHEET_ID_PARAM environment variable"
            }
            require(loadedEventLogSpreadsheetId.isNotBlank()) {
                "Missing $EVENT_LOG_SPREADSHEET_ID_PARAM environment variable"
            }
            require(loadedFormEntriesSpreadsheetId.isNotBlank()) {
                "Missing $FORM_ENTRIES_SPREADSHEET_ID_PARAM environment variable"
            }

            return FunctionLaunchParameters(
                projectName = loadedProjectName,
                storageFolderId = loadedStorageFolderId,
                timeCardSpreadsheetId = loadedTimeCardSpreadsheetId,
                eventLogSpreadsheetId = loadedEventLogSpreadsheetId,
                formEntriesSpreadsheetId = loadedFormEntriesSpreadsheetId,
            )
        }
    }
}

private const val PROJECT_NAME = "PROJECT_NAME"
private const val STORAGE_FOLDER_ID_PARAM = "STORAGE_FOLDER_ID"
private const val TIME_CARD_SPREADSHEET_ID_PARAM = "TIME_CARD_SPREADSHEET_ID"
private const val EVENT_LOG_SPREADSHEET_ID_PARAM = "EVENT_LOG_SPREADSHEET_ID"
private const val FORM_ENTRIES_SPREADSHEET_ID_PARAM = "FORM_ENTRIES_SPREADSHEET_ID"
