package com.cramsan.edifikana.client.lib.features.account.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.components.ScreenLayout
import com.cramsan.ui.theme.Padding
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Account screen.
 */
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(AccountEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            AccountEvent.Noop -> Unit
            is AccountEvent.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(event.edifikanaApplicationEvent)
            }
        }
    }

    AccountContent(
        uiState.content,
        onBackNavigation = { viewModel.navigateBack() },
        onEditClicked = { },
        onSignOutClicked = { viewModel.signOut() },
    )
}

@Composable
internal fun AccountContent(
    content: AccountUIModel,
    modifier: Modifier = Modifier,
    onBackNavigation: () -> Unit,
    onSignOutClicked: () -> Unit,
    onEditClicked: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "Account",
                onCloseClicked = onBackNavigation,
            ) {
                IconButton(onClick = onEditClicked) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = ""
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            ScreenLayout(
                sectionContent = { modifier ->
                    // First name
                    ContentLine(
                        text = content.firstName,
                        label = "First Name",
                        modifier = modifier,
                    )

                    // Last name
                    ContentLine(
                        text = content.lastName,
                        label = "Last Name",
                        modifier = modifier,
                    )

                    // Phonenumber
                    ContentLine(
                        text = content.phoneNumber,
                        label = "Phone Number",
                        modifier = modifier,
                    )

                    // Email
                    ContentLine(
                        text = content.email,
                        label = "Email",
                        modifier = modifier,
                    )
                },
                buttonContent = { modifier ->
                    // Sign Out button
                    Button(
                        modifier = modifier,
                        onClick = onSignOutClicked,
                    ) {
                        Text("Sign Out")
                    }
                },
            )
        }
    }
}

@Composable
private fun ContentLine(
    label: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(Padding.XX_SMALL),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
