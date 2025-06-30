package com.cramsan.edifikana.client.lib.features.account.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.account_screen_edit_button
import edifikana_lib.account_screen_email
import edifikana_lib.account_screen_first_name
import edifikana_lib.account_screen_last_name
import edifikana_lib.account_screen_phone_number
import edifikana_lib.account_screen_sign_out
import edifikana_lib.account_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Account screen.
 */
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.loadUserData()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AccountEvent.Noop -> Unit
            }
        }
    }

    AccountContent(
        uiState,
        onBackNavigation = { viewModel.navigateBack() },
        onEditClicked = { viewModel.editOrSave() },
        onSignOutClicked = { viewModel.signOut() },
        onFirstNameChange = { viewModel.updateFirstName(it) },
        onLastNameChange = { viewModel.updateLastName(it) },
        onEmailChange = { viewModel.updateEmail(it) },
        onPhoneNumberChange = { viewModel.updatePhoneNumber(it) },
    )
}

@Composable
internal fun AccountContent(
    content: AccountUIState,
    modifier: Modifier = Modifier,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onBackNavigation: () -> Unit,
    onSignOutClicked: () -> Unit,
    onEditClicked: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.account_screen_title),
                onNavigationIconSelected = onBackNavigation,
            ) {
                IconButton(onClick = onEditClicked) {
                    Icon(
                        imageVector = if (content.isEditable) Icons.Default.Save else Icons.Default.Edit,
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
                    val focusRequester = remember { FocusRequester() }

                    LaunchedEffect(content.isEditable) {
                        if (content.isEditable) {
                            focusRequester.requestFocus()
                        }
                    }

                    // First name
                    ContentLine(
                        value = content.firstName.orEmpty(),
                        label = stringResource(Res.string.account_screen_first_name),
                        readOnly = !content.isEditable || content.isLoading,
                        onContentChange = { onFirstNameChange(it) },
                        modifier = modifier.focusRequester(focusRequester),
                    )

                    // Last name
                    ContentLine(
                        value = content.lastName.orEmpty(),
                        label = stringResource(Res.string.account_screen_last_name),
                        readOnly = !content.isEditable || content.isLoading,
                        onContentChange = { onLastNameChange(it) },
                        modifier = modifier,
                    )

                    // Phonenumber
                    ContentLine(
                        value = content.phoneNumber.orEmpty(),
                        label = stringResource(Res.string.account_screen_phone_number),
                        readOnly = !content.isEditable || content.isLoading,
                        onContentChange = { onPhoneNumberChange(it) },
                        modifier = modifier,
                    )

                    // Email
                    ContentLine(
                        value = content.email.orEmpty(),
                        label = stringResource(Res.string.account_screen_email),
                        readOnly = !content.isEditable || content.isLoading,
                        onContentChange = { onEmailChange(it) },
                        modifier = modifier,
                    )
                },
                buttonContent = { modifier ->
                    // Sign Out button
                    Button(
                        modifier = modifier,
                        enabled = !content.isLoading && !content.isEditable,
                        onClick = onSignOutClicked,
                    ) {
                        Text(
                            text = stringResource(Res.string.account_screen_sign_out),
                        )
                    }
                },
            )
            LoadingAnimationOverlay(isLoading = content.isLoading)
        }
    }
}

@Composable
private fun ContentLine(
    label: String,
    value: String,
    readOnly: Boolean,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onContentChange(it) },
        label = { Text(label) },
        modifier = modifier,
        singleLine = true,
        readOnly = readOnly,
    )
}
