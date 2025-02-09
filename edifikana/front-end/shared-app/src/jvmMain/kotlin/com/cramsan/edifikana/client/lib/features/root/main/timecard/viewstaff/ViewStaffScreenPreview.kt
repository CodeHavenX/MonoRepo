package com.cramsan.edifikana.client.lib.features.root.main.timecard.viewstaff

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.features.main.timecard.viewstaff.ViewStaffContent
import com.cramsan.edifikana.client.lib.features.main.timecard.viewstaff.ViewStaffUIModel
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil

@Preview
@Composable
private fun ViewStaffScreenPreview() {
    AssertUtil.setInstance(NoopAssertUtil)

    ViewStaffContent(
        isLoading = true,
        staff = ViewStaffUIModel.StaffUIModel(
            fullName = "Cesar Andres Ramirez Sanchez",
            role = "Descansero",
            staffPK = StaffId("123"),
        ),
        records = listOf(
            ViewStaffUIModel.TimeCardRecordUIModel(
                eventType = "Entrada",
                timeRecorded = "2021-01-01 12:00:00",
                "storage/1",
                TimeCardEventType.CLOCK_IN,
                null,
                TimeCardEventId("123-123-123"),
                true,
            ),
            ViewStaffUIModel.TimeCardRecordUIModel(
                eventType = "Salida",
                timeRecorded = "2021-01-01 12:00:00",
                "storage/2",
                TimeCardEventType.CLOCK_OUT,
                null,
                TimeCardEventId("321"),
                false,
            ),
        ),
        onClockInClick = {},
        onClockOutClick = {},
        onShareClick = {},
    )
}
