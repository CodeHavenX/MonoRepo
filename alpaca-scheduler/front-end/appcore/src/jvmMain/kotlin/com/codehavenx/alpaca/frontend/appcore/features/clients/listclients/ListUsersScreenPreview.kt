package com.codehavenx.alpaca.frontend.appcore.features.clients.listclients

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview
@Composable
private fun ListUsersScreenPreview() {
    ListClientsContent(
        content = ClientPageUIModel(
            (0..10).map {
                ClientUIModel(
                    id = "$it",
                    displayName = "User $it",
                )
            }
        ),
        pagination = ClientPaginationUIModel(
            firstPage = "1",
            previousPage = "2",
            nextPage = "4",
            lastPage = "10",
            pages = (0..10).map {
                ClientPageReferenceUIModel(
                    displayName = "$it",
                    id = "$it",
                    selected = it == 3,
                )
            },
        ),
        loading = false,
        onClientSelected = {},
        onAddClientSelected = {},
        onPageSelected = {},
    )
}
