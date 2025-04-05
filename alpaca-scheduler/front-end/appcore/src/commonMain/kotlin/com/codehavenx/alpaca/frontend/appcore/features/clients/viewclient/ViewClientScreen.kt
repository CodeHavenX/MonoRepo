package com.codehavenx.alpaca.frontend.appcore.features.clients.viewclient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.codehavenx.alpaca.frontend.appcore.ui.components.LoadingAnimationOverlay
import com.codehavenx.alpaca.frontend.appcore.ui.theme.Padding
import org.koin.compose.koinInject

/**
 * The screen for viewing a client.
 */
@Composable
fun ViewClientScreen(
    clientId: String,
    viewModel: ViewClientViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadClient(clientId)
    }

    ViewClientContent(
        uiState.content,
        uiState.isLoading,
        onEditButtonClicked = { viewModel.editClient(it) },
    )
}

@Composable
internal fun ViewClientContent(
    content: ViewClientUIModel?,
    loading: Boolean,
    onEditButtonClicked: (String) -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Padding.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = Padding.large),
        ) {
            TextField(
                value = content?.name ?: "",
                onValueChange = { },
                label = { Text("Name") },
                readOnly = false,
                singleLine = true,
            )

            TextField(
                value = content?.email ?: "",
                onValueChange = { },
                label = { Text("Email") },
                readOnly = false,
                singleLine = true,
            )

            TextField(
                value = content?.phone ?: "",
                onValueChange = { },
                label = { Text("Phone number") },
                readOnly = false,
                singleLine = true,
            )

            HorizontalDivider()

            TextField(
                value = content?.address ?: "",
                onValueChange = { },
                label = { Text("Street Address") },
                readOnly = false,
                singleLine = true,
            )

            TextField(
                value = content?.address ?: "",
                onValueChange = { },
                label = { Text("Apt, suite, etc") },
                readOnly = false,
                singleLine = true,
            )

            TextField(
                value = content?.city ?: "",
                onValueChange = { },
                label = { Text("City") },
                readOnly = false,
                singleLine = true,
            )

            TextField(
                value = content?.state ?: "",
                onValueChange = { },
                label = { Text("State") },
                readOnly = false,
                singleLine = true,
            )

            TextField(
                value = content?.zip ?: "",
                onValueChange = { },
                label = { Text("Zip Code") },
                readOnly = false,
                singleLine = true,
            )

            Button(onClick = {
                content?.id?.let {
                    onEditButtonClicked(it)
                }
            }) {
                Text("Edit")
            }
        }
        LoadingAnimationOverlay(isLoading = loading)
    }
}
