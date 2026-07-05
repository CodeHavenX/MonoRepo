package com.cramsan.edifikana.client.lib.features.auth.setnewpassword

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaPasswordTextField
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.change_password_dialog_confirm_password
import edifikana_lib.change_password_dialog_new_password
import edifikana_lib.set_new_password_screen_submit_button
import edifikana_lib.set_new_password_screen_subtitle
import edifikana_lib.set_new_password_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Set new password screen — shown after the user arrives via the recovery deep link.
 */
@Composable
fun SetNewPasswordScreen(
    destination: AuthDestination.SetNewPasswordDestination,
    viewModel: SetNewPasswordViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(destination)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            SetNewPasswordEvent.Noop -> Unit
        }
    }

    SetNewPasswordContent(
        uiState = uiState,
        onNewPasswordChange = { viewModel.onNewPasswordChange(it) },
        onConfirmPasswordChange = { viewModel.onConfirmPasswordChange(it) },
        onSubmitSelected = { viewModel.onSubmitSelected() },
        onCloseClicked = { viewModel.navigateBack() },
    )
}

/**
 * Content of the set new password screen.
 */
@OptIn(SecureStringAccess::class)
@Composable
internal fun SetNewPasswordContent(
    uiState: SetNewPasswordUIState,
    modifier: Modifier = Modifier,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmitSelected: () -> Unit,
    onCloseClicked: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.set_new_password_screen_title),
                onNavigationIconSelected = onCloseClicked,
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            sectionContent = { sectionModifier ->
                Text(
                    text = stringResource(Res.string.set_new_password_screen_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = sectionModifier,
                )

                EdifikanaPasswordTextField(
                    value = uiState.newPassword.reveal(),
                    onValueChange = onNewPasswordChange,
                    label = stringResource(Res.string.change_password_dialog_new_password),
                    modifier = sectionModifier,
                    supportingText = {
                        uiState.newPasswordMessage?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                )

                EdifikanaPasswordTextField(
                    value = uiState.confirmPassword.reveal(),
                    onValueChange = onConfirmPasswordChange,
                    label = stringResource(Res.string.change_password_dialog_confirm_password),
                    modifier = sectionModifier,
                    supportingText = {
                        uiState.confirmPasswordMessage?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                )
            },
            buttonContent = { buttonModifier ->
                EdifikanaPrimaryButton(
                    text = stringResource(Res.string.set_new_password_screen_submit_button),
                    onClick = onSubmitSelected,
                    enabled = uiState.submitEnabled && !uiState.isLoading,
                    modifier = buttonModifier,
                )
            },
            overlay = {
                LoadingAnimationOverlay(uiState.isLoading)
            },
            contentAlignment = Alignment.Center,
        )
    }
}
