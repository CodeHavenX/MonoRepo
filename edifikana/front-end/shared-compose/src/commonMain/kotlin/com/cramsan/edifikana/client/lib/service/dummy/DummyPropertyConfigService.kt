@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.PropertyModel
import kotlinx.datetime.TimeZone

/**
 * Dummy implementation of [PropertyConfigService] for testing purposes.
 */
class DummyPropertyConfigService : PropertyConfigService {
    override suspend fun getPropertyConfig(): Result<PropertyModel> {
        return Result.success(
            PropertyModel(
                propertyId = "1",
                driveFolderId = "1",
                storageFolderId = "1",
                timeCardSpreadsheetId = "1",
                eventLogSpreadsheetId = "1",
                formEntriesSpreadsheetId = "1",
                timeZone = TimeZone.UTC,
            )
        )
    }
}
