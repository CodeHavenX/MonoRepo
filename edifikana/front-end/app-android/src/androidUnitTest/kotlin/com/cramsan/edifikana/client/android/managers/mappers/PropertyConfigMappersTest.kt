@file:OptIn(FireStoreModel::class)

package com.cramsan.edifikana.client.android.managers.mappers

import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.models.PropertyConfigModel
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.PropertyConfig
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class PropertyConfigMappersTest {

    @Test
    fun `toDomainModel should map PropertyConfig to PropertyConfigModel correctly`() {
        val propertyConfig = PropertyConfig(
            propertyId = "propertyId",
            driveFolderId = "driveFolderId",
            storageFolderId = "storageFolderId",
            timeCardSpreadsheetId = "timeCardSpreadsheetId",
            eventLogSpreadsheetId = "eventLogSpreadsheetId",
            formEntriesSpreadsheetId = "formEntriesSpreadsheetId",
            timeZone = "America/Los_Angeles"
        )

        val result = propertyConfig.toDomainModel()

        assertEquals("propertyId", result.propertyId)
        assertEquals("driveFolderId", result.driveFolderId)
        assertEquals("storageFolderId", result.storageFolderId)
        assertEquals("timeCardSpreadsheetId", result.timeCardSpreadsheetId)
        assertEquals("eventLogSpreadsheetId", result.eventLogSpreadsheetId)
        assertEquals("formEntriesSpreadsheetId", result.formEntriesSpreadsheetId)
        assertEquals("America/Los_Angeles", result.timeZone.id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toDomainModel should throw IllegalArgumentException when propertyId is blank`() {
        val propertyConfig = PropertyConfig(
            propertyId = "",
            driveFolderId = "driveFolderId",
            storageFolderId = "storageFolderId",
            timeCardSpreadsheetId = "timeCardSpreadsheetId",
            eventLogSpreadsheetId = "eventLogSpreadsheetId",
            formEntriesSpreadsheetId = "formEntriesSpreadsheetId",
            timeZone = "timeZone",
        )

        propertyConfig.toDomainModel()
    }

    @Test
    fun `toFirebaseModel should map PropertyConfigModel to PropertyConfig correctly`() {
        val timeZone: TimeZone = mockk()
        every { timeZone.id } returns "America/North_Dakota/New_Salem"

        val propertyConfigModel = PropertyConfigModel(
            propertyId = "propertyId",
            driveFolderId = "driveFolderId",
            storageFolderId = "storageFolderId",
            timeCardSpreadsheetId = "timeCardSpreadsheetId",
            eventLogSpreadsheetId = "eventLogSpreadsheetId",
            formEntriesSpreadsheetId = "formEntriesSpreadsheetId",
            timeZone = timeZone,
        )

        val result = propertyConfigModel.toFirebaseModel()

        assertEquals("propertyId", result.propertyId)
        assertEquals("driveFolderId", result.driveFolderId)
        assertEquals("storageFolderId", result.storageFolderId)
        assertEquals("timeCardSpreadsheetId", result.timeCardSpreadsheetId)
        assertEquals("eventLogSpreadsheetId", result.eventLogSpreadsheetId)
        assertEquals("formEntriesSpreadsheetId", result.formEntriesSpreadsheetId)
        assertEquals("America/North_Dakota/New_Salem", result.timeZone)
    }
}
