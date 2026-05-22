package com.cramsan.flyerboard.client.lib.features.splash

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun SplashPreview() =
    AppTheme(dynamicColor = false) {
        SplashContent(
            content = SplashUIState(isLoading = true),
        )
    }
