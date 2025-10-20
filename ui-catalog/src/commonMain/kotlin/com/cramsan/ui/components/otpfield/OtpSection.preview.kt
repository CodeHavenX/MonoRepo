package com.cramsan.ui.components.otpfield

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun OtpSectionPreviewEmpty() {
    OtpSection(otpCode = "", onValueChanged = {})
}

@Composable
@Preview
fun OtpSectionPreviewFull() {
    OtpSection(otpCode = "123456", onValueChanged = {})
}
