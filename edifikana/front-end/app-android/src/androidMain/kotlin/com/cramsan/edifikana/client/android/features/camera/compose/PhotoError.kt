package com.cramsan.edifikana.client.android.features.camera.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cramsan.edifikana.client.android.R

@Composable
fun PhotoError(
    message: String,
    onCancelClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = message,
            modifier = Modifier.align(Alignment.Center),
        )
        BottomActionBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            mainButton = {
                IconButton(onClick = onCancelClick) {
                    Icon(
                        imageVector = Icons.Sharp.Cancel,
                        contentDescription = stringResource(R.string.string_cancel),
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
private fun PreviewPhotoConfirmation() {
    PhotoError("Hubo un error") {}
}
