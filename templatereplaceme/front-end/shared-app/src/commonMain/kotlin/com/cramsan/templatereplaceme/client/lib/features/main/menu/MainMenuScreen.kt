package com.cramsan.templatereplaceme.client.lib.features.main.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeApplicationViewModel
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import templatereplaceme_lib.Res
import templatereplaceme_lib.main_menu_screen_text_create_account
import templatereplaceme_lib.main_menu_screen_text_first_name
import templatereplaceme_lib.main_menu_screen_text_last_name

/**
 * Sign In screen
 */
@Composable
fun MainMenuScreen(
    viewModel: MainMenuViewModel = koinViewModel(),
    applicationViewModel: TemplateReplaceMeApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val appUiState by applicationViewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        //
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            MainMenuEvent.Noop -> Unit
        }
    }

    MainMenuContent(
        uiState = uiState,
        isMultiWindowEnabled = appUiState.showDebugWindow,
        modifier = Modifier,
        onFirstnameValueChange = { viewModel.changeFirstNameValue(it) },
        onLastNameValueChange = { viewModel.changeLastNameValue(it) },
        onMultiWindowToggled = { applicationViewModel.setShowDebugWindow(it) },
        onClicked = { viewModel.createAccount() },
    )
}

@Composable
internal fun MainMenuContent(
    uiState: MainMenuUIState,
    isMultiWindowEnabled: Boolean,
    modifier: Modifier = Modifier,
    onFirstnameValueChange: (String) -> Unit,
    onLastNameValueChange: (String) -> Unit,
    onMultiWindowToggled: (Boolean) -> Unit,
    onClicked: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        // Render the screen
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ScreenLayout(
                sectionContent = { modifier ->
                    OutlinedTextField(
                        value = uiState.firstName,
                        onValueChange = onFirstnameValueChange,
                        label = {
                            Text(stringResource(Res.string.main_menu_screen_text_first_name))
                        },
                        modifier = modifier,
                    )
                    OutlinedTextField(
                        value = uiState.lastName,
                        onValueChange = onLastNameValueChange,
                        label = {
                            Text(stringResource(Res.string.main_menu_screen_text_last_name))
                        },
                        modifier = modifier,
                    )
                },
                buttonContent = { modifier ->
                    Button(
                        onClick = onClicked,
                        modifier = modifier,
                    ) {
                        Text(
                            stringResource(Res.string.main_menu_screen_text_create_account),
                        )
                    }
                },
            )
            Row(
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Multi-window")
                Spacer(Modifier.width(4.dp))
                Switch(
                    checked = isMultiWindowEnabled,
                    onCheckedChange = onMultiWindowToggled,
                )
            }
        }
    }
    LoadingAnimationOverlay(uiState.isLoading)
}
