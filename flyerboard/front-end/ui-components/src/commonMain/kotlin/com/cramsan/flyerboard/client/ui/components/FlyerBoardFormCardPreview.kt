package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ComponentPreviews
import com.cramsan.ui.preview.DevicePreviews

@DevicePreviews
@Composable
private fun FlyerBoardFormCardPreview() =
    AppTheme {
        FlyerBoardFormCard(
            title = "Title",
            subtitle = "Subtitle",
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Field") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("Submit")
            }
        }
    }

@ComponentPreviews
@Composable
private fun FlyerBoardFormCardLoadingPreview() =
    AppTheme {
        FlyerBoardFormCard(
            title = "Title",
            subtitle = "Subtitle",
            isLoading = true,
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Field") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("Submit")
            }
        }
    }
