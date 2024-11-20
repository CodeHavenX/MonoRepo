package com.codehavenx.alpaca.frontend.appcore.features.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeContent(
        content = HomeUIModel(""),
        loading = false,
    )
}
