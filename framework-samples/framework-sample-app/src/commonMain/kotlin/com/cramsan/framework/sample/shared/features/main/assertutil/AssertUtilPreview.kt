package com.cramsan.framework.sample.shared.features.main.assertutil

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
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
