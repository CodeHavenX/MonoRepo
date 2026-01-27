package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.cramsan.ui.theme.Padding
import org.jetbrains.compose.resources.stringResource
import ui_catalog.Res
import ui_catalog.password_outlined_textfield_toggle_visibility_description_hidden
import ui_catalog.password_outlined_textfield_toggle_visibility_description_visible

/**
 * Edifikana styled password text field component with visibility toggle.
 *
 * @param value The current text value
 * @param onValueChange Callback when the text changes
 * @param modifier Modifier for the text field
 * @param placeholder Placeholder text to display when empty
 * @param label Optional label to display above the text field
 * @param enabled Whether the field is enabled
 */
@Composable
fun EdifikanaPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    enabled: Boolean = true,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
) {
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
    ) {
        // If label is provided, show it above the text field
        if (!label.isNullOrBlank()) {
            Text(
                label,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        OutlinedTextField(
            value = value,
            //TODO: Update this so we are dynamically filling the screen instead
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                Icon(
                    if (showPassword) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    },
                    contentDescription = if (showPassword) {
                        stringResource(Res.string.password_outlined_textfield_toggle_visibility_description_visible)
                    } else {
                        stringResource(Res.string.password_outlined_textfield_toggle_visibility_description_hidden)
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { showPassword = !showPassword }
                        .padding(Padding.XX_SMALL)
                )
            },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            enabled = enabled,
            supportingText = supportingText,
            isError = isError,
            singleLine = true,
            maxLines = 1,
            minLines = 1,
            shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
            textStyle = MaterialTheme.typography.bodyLarge,
        )
    }
}
