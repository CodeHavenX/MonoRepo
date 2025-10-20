package com.cramsan.ui.components.otpfield

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

/**
 * OTP input field.
 *
 * This component represents a single input field for entering an OTP code.
 *
 * @param otpCode The current OTP code value.
 * @param onValueChanged Callback invoked when the OTP code value changes.
 * @param modifier Optional [Modifier] for this component.
 */
@Composable
fun OtpSection(
    otpCode: String,
    onValueChanged: (String) -> Unit,
    length: Int = OTP_LENGTH,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = otpCode,
        onValueChange = {
            val sanitizedText = it.filter { char -> char.isDigit() }.take(length)
            onValueChanged(sanitizedText)
        },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        modifier = modifier,
    )
}

private const val OTP_LENGTH = 6
