package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun FlyerCardWithExpiryPreview() = AppTheme {
    FlyerCard(
        title = "Community Yard Sale",
        description = "Everything must go! Furniture, clothes, books and more.",
        expiresAt = "2026-05-01",
        onClick = {},
    )
}

@Preview
@Composable
private fun FlyerCardNoExpiryPreview() = AppTheme {
    FlyerCard(
        title = "Lost Cat – Please Help",
        description = "Orange tabby, answers to Mango. Last seen near Oak Street.",
        onClick = {},
    )
}
