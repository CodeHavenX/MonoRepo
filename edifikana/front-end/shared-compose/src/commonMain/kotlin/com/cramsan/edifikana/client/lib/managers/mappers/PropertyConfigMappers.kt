package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.models.PropertyConfigModel
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.PropertyConfig
import com.cramsan.edifikana.lib.requireNotBlank
import com.cramsan.edifikana.lib.safeTimeZone

@FireStoreModel
fun PropertyConfig.toDomainModel(): PropertyConfigModel {
    return PropertyConfigModel(
        propertyId = requireNotBlank(propertyId),
        driveFolderId = requireNotBlank(driveFolderId),
        storageFolderId = requireNotBlank(storageFolderId),
        timeCardSpreadsheetId = requireNotBlank(timeCardSpreadsheetId),
        eventLogSpreadsheetId = requireNotBlank(eventLogSpreadsheetId),
        formEntriesSpreadsheetId = requireNotBlank(formEntriesSpreadsheetId),
        timeZone = safeTimeZone(timeZone),
    )
}

@FireStoreModel
fun PropertyConfigModel.toFirebaseModel() = PropertyConfig(
    propertyId = propertyId,
    driveFolderId = driveFolderId,
    storageFolderId = storageFolderId,
    timeCardSpreadsheetId = timeCardSpreadsheetId,
    eventLogSpreadsheetId = eventLogSpreadsheetId,
    formEntriesSpreadsheetId = formEntriesSpreadsheetId,
    timeZone = timeZone.id,
)
