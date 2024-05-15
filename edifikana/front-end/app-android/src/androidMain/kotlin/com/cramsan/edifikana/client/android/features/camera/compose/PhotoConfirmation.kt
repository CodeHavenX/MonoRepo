package com.cramsan.edifikana.client.android.features.camera.compose

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Cancel
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.cramsan.edifikana.client.android.R

@Composable
fun PhotoConfirmation(
    photoUri: Uri,
    onCancelClick: () -> Unit,
    onConfirmClick: (Uri) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter(photoUri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        BottomActionBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            mainButton = {
                IconButton(onClick = { onConfirmClick(photoUri) }) {
                    Icon(
                        imageVector = Icons.Sharp.Check,
                        contentDescription = stringResource(R.string.string_confirm),
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            },
            secondaryButton = {
                IconButton(onClick = onCancelClick) {
                    Icon(
                        imageVector = Icons.Sharp.Cancel,
                        contentDescription = stringResource(R.string.string_cancel),
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize()
                            .padding(5.dp)
                    )
                }
            }
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun PreviewPhotoConfirmation() {
    PhotoConfirmation(
        Uri.parse(""),
        onConfirmClick = {},
        onCancelClick = {},
    )
}
