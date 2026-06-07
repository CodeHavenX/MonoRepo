package com.cramsan.framework.sample.shared.features.main.assertutil

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
private fun AssertUtilScreenPreview() {
    AssertUtilContent(
        onAssertTrue = {},
        onAssertFalse = {},
        onAssertFalsePasses = {},
        onAssertFalseFails = {},
        onAssertNullPasses = {},
        onAssertNotNullPasses = {},
        onAssertFailure = {},
        onBack = {},
    )
}
