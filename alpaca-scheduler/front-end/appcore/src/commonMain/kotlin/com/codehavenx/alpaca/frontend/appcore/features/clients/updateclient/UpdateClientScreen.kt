package com.codehavenx.alpaca.frontend.appcore.features.clients.updateclient

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.codehavenx.alpaca.frontend.appcore.ui.components.LoadingAnimationOverlay
import com.codehavenx.alpaca.frontend.appcore.ui.theme.Padding
import org.koin.compose.koinInject

/**
 * The Update Client screen.
 */
@Composable
fun UpdateClientScreen(
    clientId: String,
    viewModel: UpdateClientViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadClient(clientId)
    }

    UpdateClientContent(
        uiState.content,
        uiState.isLoading,
        onUpdateClientButtonClicked = { viewModel.updateClient() },
    )
}

@Composable
@Suppress("UnusedParameter")
internal fun UpdateClientContent(
    content: UpdateClientUIModel?,
    loading: Boolean,
    onUpdateClientButtonClicked: () -> Unit = {},
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
            var name by remember { mutableStateOf("Cesar Ramirez") }
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
            )

            var email by remember { mutableStateOf("admin@test.com") }
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
            )

            var phone by remember { mutableStateOf("7592945") }
            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone number") },
                singleLine = true,
            )

            HorizontalDivider()

            var streetAddress by remember { mutableStateOf("") }
            TextField(
                value = streetAddress,
                onValueChange = { streetAddress = it },
                label = { Text("Street Address") },
                singleLine = true,
            )

            var aptSuite by remember { mutableStateOf("") }
            TextField(
                value = aptSuite,
                onValueChange = { aptSuite = it },
                label = { Text("Apt, suite, etc") },
                singleLine = true,
            )

            var city by remember { mutableStateOf("") }
            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                singleLine = true,
            )

            var state by remember { mutableStateOf("") }
            TextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("State") },
                singleLine = true,
            )

            var zipCode by remember { mutableStateOf("") }
            TextField(
                value = zipCode,
                onValueChange = { zipCode = it },
                label = { Text("Zip Code") },
                singleLine = true,
            )

            Button(onClick = onUpdateClientButtonClicked) {
                Text("Save Changes")
            }
        }
        LoadingAnimationOverlay(isLoading = loading)
    }
}
