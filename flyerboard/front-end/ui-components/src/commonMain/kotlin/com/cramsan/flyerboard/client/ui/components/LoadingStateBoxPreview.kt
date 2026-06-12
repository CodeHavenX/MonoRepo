package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
private fun LoadingStateBoxPreview() =
    AppTheme {
        Box(modifier = Modifier.size(200.dp)) {
            LoadingStateBox()
        }
    }
