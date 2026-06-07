package com.cramsan.ui.components.otpfield

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ComponentPreviews

@Composable
@ComponentPreviews
fun OtpSectionPreviewEmpty() {
    OtpSection(otpCode = "", onValueChanged = {})
}

@Composable
@ComponentPreviews
fun OtpSectionPreviewFull() {
    OtpSection(otpCode = "123456", onValueChanged = {})
}
