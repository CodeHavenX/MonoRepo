package com.cramsan.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import org.jetbrains.compose.resources.stringResource
import ui_catalog.Res
import ui_catalog.password_outlined_textfield_toggle_visibility_description_hidden
import ui_catalog.password_outlined_textfield_toggle_visibility_description_visible

/**
 * A password [OutlinedTextField] that toggles visibility of the password.
 *
 * @param value The current value of the text field.
 * @param onValueChange The callback that is called when the value changes.
 * @param modifier The modifier to apply to the text field.
 * @param enabled Whether the text field is enabled.
 * @param readOnly Whether the text field is read-only.
 * @param textStyle The text style to apply to the text field.
 * @param label The label to display in the text field.
 * @param placeholder The placeholder to display in the text field.
 * @param leadingIcon The icon to display at the start of the text field.
 * @param prefix The content to display before the text field.
 * @param suffix The content to display after the text field.
 * @param supportingText The supporting text to display below the text field.
 * @param isError Whether the text field is in an error state.
 * @param keyboardOptions The keyboard options to apply to the text field.
 * @param keyboardActions The keyboard actions to apply to the text field.
 * @param interactionSource The interaction source to apply to the text field.
 * @param shape The shape to apply to the text field.
 * @param colors The colors to apply to the text field.
 */
@Suppress("LongParameterList")
@Composable
fun PasswordOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        autoCorrectEnabled = false,
        keyboardType = KeyboardType.Password
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    var showPassword by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
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
                    .clickable { showPassword = !showPassword }
            )
        },
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        maxLines = 1,
        minLines = 1,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
    )
}
