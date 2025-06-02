@file:Suppress("MaximumLineLength")

package com.cramsan.edifikana.client.lib.features.auth.validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ValidationScreenPreview() = AppTheme {
    OtpValidationContent(
        uiState = OtpValidationUIState(
            isLoading = true,
            errorMessage = "",
            otpCode = "123456",
        ),
        modifier = Modifier,
        onBackClicked = {}
    )
}