package com.cramsan.ui.components.otpfield

// Using fully-qualified references for KeyboardOptions/KeyboardType to avoid import resolution issues in this environment
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

/**
 * OTP input field.
 *
 * This component represents a single input field for entering an OTP code.
 *
 * @param otpCode The current OTP code value.
 * @param onValueChanged Callback invoked when the OTP code value changes.
 * @param length Maximum number of digits allowed; input will be sanitized and truncated to this length.
 *               Prefer using `com.cramsan.framework.core.OtpConstants.OTP_LENGTH` as a single source of truth.
 * @param modifier Optional [Modifier] for this component.
 */
@Composable
fun OtpSection(
    otpCode: String,
    onValueChanged: (String) -> Unit,
    length: Int = DEFAULT_OTP_LENGTH,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = otpCode,
        onValueChange = {
            val sanitizedText = it.filter { char -> char.isDigit() }.take(length)
            onValueChanged(sanitizedText)
        },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        label = { Text("Verification code") },
        modifier = modifier,
    )
}

private const val DEFAULT_OTP_LENGTH: Int = 6
