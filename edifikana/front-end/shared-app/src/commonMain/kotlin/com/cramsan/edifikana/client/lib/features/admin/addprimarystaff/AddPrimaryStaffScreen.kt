package com.cramsan.edifikana.client.lib.features.admin.addprimarystaff

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * AddPrimaryStaff screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun AddPrimaryStaffScreen(
    modifier: Modifier = Modifier,
    viewModel: AddPrimaryStaffViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    val screenScope = rememberCoroutineScope()
    screenScope.launch {
        viewModel.events.collect { event ->
            when (event) {
                AddPrimaryStaffEvent.Noop -> Unit
            }
        }
    }

    // Render the screen
    AddPrimaryStaffContent(
        uiState,
        modifier,
        onBackSelected = {
            viewModel.navigateBack()
        },
        onInviteSelected = {
            viewModel.invite(it)
        }
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun AddPrimaryStaffContent(
    content: AddPrimaryStaffUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onInviteSelected: (String) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = content.title,
                onNavigationIconSelected = onBackSelected,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
            ScreenLayout(
                sectionContent = { sectionModifier ->
                    AnimatedContent(
                        content.errorMessage,
                        modifier = modifier,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                    ) {
                        if (!it.isNullOrBlank()) {
                            Text(
                                content.errorMessage.orEmpty(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        label = { Text("Email") },
                        modifier = sectionModifier,
                        isError = !content.errorMessage.isNullOrBlank(),
                    )
                },
                buttonContent = { buttonModifier ->
                    Button(
                        modifier = buttonModifier,
                        onClick = { onInviteSelected(textFieldValue.text) },
                    ) {
                        Text("Invite")
                    }
                }
            )
            LoadingAnimationOverlay(content.isLoading)
        }
    }
}
