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
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.account_screen_edit_button
import edifikana_lib.account_screen_email
import edifikana_lib.account_screen_first_name
import edifikana_lib.account_screen_last_name
import edifikana_lib.account_screen_phone_number
import edifikana_lib.account_screen_sign_out
import edifikana_lib.account_screen_title
import org.jetbrains.compose.resources.stringResource
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
                title = stringResource(Res.string.account_screen_title),
                onCloseClicked = onBackNavigation,
            ) {
                IconButton(onClick = onEditClicked) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(Res.string.account_screen_edit_button),
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
                        label = stringResource(Res.string.account_screen_first_name),
                        modifier = modifier,
                    )

                    // Last name
                    ContentLine(
                        text = content.lastName,
                        label = stringResource(Res.string.account_screen_last_name),
                        modifier = modifier,
                    )

                    // Phonenumber
                    ContentLine(
                        text = content.phoneNumber,
                        label = stringResource(Res.string.account_screen_phone_number),
                        modifier = modifier,
                    )

                    // Email
                    ContentLine(
                        text = content.email,
                        label = stringResource(Res.string.account_screen_email),
                        modifier = modifier,
                    )
                },
                buttonContent = { modifier ->
                    // Sign Out button
                    Button(
                        modifier = modifier,
                        onClick = onSignOutClicked,
                    ) {
                        Text(
                            text = stringResource(Res.string.account_screen_sign_out),
                        )
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
