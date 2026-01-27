package com.cramsan.edifikana.client.lib.features.account.changepassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaPasswordTextField
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.theme.Padding
import org.koin.compose.viewmodel.koinViewModel

/** * Composable function to render a dialog for changing the user's password.
 *
 * @param viewModel The ViewModel that manages the state and logic for the change password dialog.
 */
@Composable
fun ChangePasswordDialog(
    viewModel: ChangePasswordDialogViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.loadUserData()
    }

    Dialog(
        onDismissRequest = { viewModel.navigateBack() },
    ) {
        RenderContent(
            uiState,
            onCurrentPasswordChange = { viewModel.onCurrentPasswordChange(it) },
            onNewPasswordChange = { viewModel.onNewPasswordChange(it) },
            onConfirmPasswordChange = { viewModel.onConfirmPasswordChange(it) },
            onSubmitSelected = { viewModel.onSubmitSelected() },
        )
    }
}

@OptIn(SecureStringAccess::class)
@Composable
internal fun RenderContent(
    uiState: ChangePasswordDialogUIState,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmitSelected: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(Padding.MEDIUM),
            ) {
                Text(
                    text = "Change Password",
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Start,
                )
                if (uiState.showCurrentPassword) {
                    EdifikanaPasswordTextField(
                        value = uiState.currentPassword.reveal(),
                        onValueChange = {
                            onCurrentPasswordChange(it)
                        },
                        label = "Current password",
                        modifier = Modifier
                            .fillMaxWidth(),
                        supportingText = {
                            uiState.currentPasswordMessage?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (uiState.currentPasswordInError) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                )
                            }
                        }
                    )
                }
                EdifikanaPasswordTextField(
                    value = uiState.newPassword.reveal(),
                    onValueChange = {
                        onNewPasswordChange(it)
                    },
                    label = "New password",
                    modifier = Modifier
                        .fillMaxWidth(),
                    supportingText = {
                        uiState.newPasswordMessage?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                )
                EdifikanaPasswordTextField(
                    value = uiState.confirmPassword.reveal(),
                    onValueChange = {
                        onConfirmPasswordChange(it)
                    },
                    label = "Confirm password",
                    modifier = Modifier
                        .fillMaxWidth(),
                    supportingText = {
                        uiState.confirmPasswordMessage?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        enabled = uiState.submitEnabled && !uiState.isLoading,
                        onClick = { onSubmitSelected() },
                    ) {
                        Text("Submit")
                    }
                }
            }
            LoadingAnimationOverlay(uiState.isLoading)
        }
    }
}
