package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardNavyBackground
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
private fun FlyerBoardWordmarkPreview() =
    AppTheme {
        FlyerBoardWordmark(modifier = Modifier.background(FlyerBoardNavyBackground))
    }
