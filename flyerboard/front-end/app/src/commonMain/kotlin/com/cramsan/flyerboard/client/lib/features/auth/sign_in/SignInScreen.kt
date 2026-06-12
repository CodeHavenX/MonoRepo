package com.cramsan.flyerboard.client.lib.features.auth.sign_in

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.cramsan.flyerboard.client.ui.components.FlyerBoardFormCard
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import flyerboard_lib.Res
import flyerboard_lib.sign_in_screen_button_sign_in
import flyerboard_lib.sign_in_screen_forgot_password
import flyerboard_lib.sign_in_screen_label_email
import flyerboard_lib.sign_in_screen_label_password
import flyerboard_lib.sign_in_screen_no_account
import flyerboard_lib.sign_in_screen_remember_me
import flyerboard_lib.sign_in_screen_sign_up_link
import flyerboard_lib.sign_in_screen_subtitle
import flyerboard_lib.sign_in_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Sign In screen.
 */
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            SignInEvent.Noop -> Unit
        }
    }

    SignInContent(
        uiState = uiState,
        modifier = modifier,
        onEmailChanged = { viewModel.onEmailChanged(it) },
        onPasswordChanged = { viewModel.onPasswordChanged(it) },
        onSignInClicked = { viewModel.signIn() },
        onSignUpClicked = { viewModel.navigateToSignUp() },
        onDebugIconClicked = { viewModel.navigateToDebugSettings() },
    )
}

/**
 * Content of the Sign In screen.
 */
@Composable
internal fun SignInContent(
    uiState: SignInUIState,
    modifier: Modifier = Modifier,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignInClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    onDebugIconClicked: () -> Unit,
) {
    var rememberMe by remember { mutableStateOf(false) }

    FlyerBoardFormCard(
        title = stringResource(Res.string.sign_in_screen_title),
        subtitle = stringResource(Res.string.sign_in_screen_subtitle),
        modifier = modifier,
        isLoading = uiState.isLoading,
        overlayContent = {
            IconButton(
                onClick = onDebugIconClicked,
                modifier = Modifier.align(Alignment.BottomEnd),
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                )
            }
        },
    ) {
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChanged,
            label = { Text(stringResource(Res.string.sign_in_screen_label_email)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            shape = CircleShape,
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = { Text(stringResource(Res.string.sign_in_screen_label_password)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            shape = CircleShape,
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                )
                Text(
                    text = stringResource(Res.string.sign_in_screen_remember_me),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            TextButton(onClick = {}) {
                Text(
                    text = stringResource(Res.string.sign_in_screen_forgot_password),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Button(
            onClick = onSignInClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text(stringResource(Res.string.sign_in_screen_button_sign_in))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.sign_in_screen_no_account),
                style = MaterialTheme.typography.bodyMedium,
            )
            TextButton(onClick = onSignUpClicked) {
                Text(
                    text = stringResource(Res.string.sign_in_screen_sign_up_link),
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
