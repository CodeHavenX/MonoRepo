package com.cramsan.edifikana.client.lib.features.main.camera.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edifikana_lib.Res
import edifikana_lib.string_cancel
import edifikana_lib.text_permissions_camera
import edifikana_lib.text_permissions_open_setting
import org.jetbrains.compose.resources.stringResource

/**
 * Screen to show when the user denies the camera permission.
 */
@Composable
fun PermissionDeniedScreen(
    onOpenSettingsClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .wrapContentSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.text_permissions_camera),
            )
            Button(onClick = onOpenSettingsClick) {
                Text(
                    text = stringResource(Res.string.text_permissions_open_setting),
                )
            }
        }
        BottomActionBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            mainButton = {
                IconButton(onClick = onCancelClick) {
                    Icon(
                        imageVector = Icons.Sharp.Cancel,
                        contentDescription = stringResource(Res.string.string_cancel),
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                    )
                }
            },
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun PreviewPermissionDeniedScreen() {
    PermissionDeniedScreen(
        onOpenSettingsClick = {},
        onCancelClick = {},
    )
}
