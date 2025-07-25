package com.cramsan.edifikana.client.lib.features.debug.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Debug feature screen.
 */
@Preview
@Composable
private fun DebugScreenPreview() = AppTheme {
    DebugContent(
        content = DebugUIModelUI(
            listOf(
                Field.Label("This is the first section"),
                Field.StringField(
                    title = "Override host URL",
                    subtitle = "Provide a host URL to point to.",
                    key = "string_key",
                    value = "http://10.0.0.1:1090",
                ),
                Field.StringField(
                    title = "Welcome message",
                    subtitle = null,
                    key = "string_key_2",
                    value = "",
                ),
                Field.Divider,
                Field.Label("This is the second section"),
                Field.BooleanField(
                    title = "Halt on failure",
                    subtitle = "Enable the halt-on-faiilure mechanism when in a supported platform. " +
                        "This will cause the application to freeze when an error is found. Allowing " +
                        "you the chance to connect the debugger and inspect tha application state.",
                    key = "debug_key_2",
                    value = true,
                ),
                Field.BooleanField(
                    title = "Disable cache",
                    subtitle = null,
                    key = "debug_key",
                    value = false,
                ),
                Field.BooleanField(
                    title = "Apply debug settings",
                    subtitle = "Currently disabled",
                    key = "debug_key",
                    value = true,
                    enabled = false,
                ),
            )
        ),
        modifier = Modifier,
        bufferChanges = { _, _ -> },
        saveChanges = { _, _ -> },
        onCloseSelected = {},
        onAction = { }
    )
}
