package com.cramsan.edifikana.client.lib.features.management.viewstaff

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ViewStaffScreenPreview() = AppTheme(debugLayoutInspection = true) {
    AssertUtil.setInstance(NoopAssertUtil())

    ViewStaffContent(
        ViewStaffUIState(
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
            title = "",
        ),
        onClockInClick = {},
        onClockOutClick = {},
        onShareClick = {},
        onCloseSelected = {},
    )
}
