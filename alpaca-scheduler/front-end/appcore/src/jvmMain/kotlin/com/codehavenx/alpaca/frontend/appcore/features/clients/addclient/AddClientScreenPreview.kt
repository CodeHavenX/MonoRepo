package com.codehavenx.alpaca.frontend.appcore.features.clients.addclient

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview
@Composable
private fun AddClientScreenPreview() {
    AddClientContent(
        content = AddClientUIModel(""),
    )
}
