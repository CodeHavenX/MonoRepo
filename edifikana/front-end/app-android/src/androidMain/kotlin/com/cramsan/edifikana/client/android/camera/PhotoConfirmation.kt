package com.cramsan.edifikana.client.android.camera

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Cancel
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun PhotoConfirmation(
    photoUri: Uri,
    onCancel: () -> Unit,
    onConfirm: (Uri) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = rememberImagePainter(photoUri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onConfirm(photoUri) }
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 4.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onConfirm(photoUri) }) {
                Icon(
                    imageVector = Icons.Sharp.Check,
                    contentDescription = "Confirmar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(100.dp)
                )
            }
            IconButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Sharp.Cancel,
                    contentDescription = "Cancelar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(100.dp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun PreviewPhotoConfirmation() {
    PhotoConfirmation(
        Uri.parse(""),
        onConfirm = {},
        onCancel = {},
    )
}