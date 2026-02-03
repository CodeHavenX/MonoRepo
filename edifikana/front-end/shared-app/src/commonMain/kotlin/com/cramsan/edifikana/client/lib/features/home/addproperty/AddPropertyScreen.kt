package com.cramsan.edifikana.client.lib.features.home.addproperty

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.architecture.client.di.WindowIdentifier
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.home.shared.PropertyIconOptions
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowDelegatedEvent
import com.cramsan.edifikana.client.ui.components.EdifikanaImageSelector
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTextField
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.ui.ObserveEventEmitterEvents
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.text_add
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

/**
 * AddProperty screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun AddPropertyScreen(
    destination: HomeDestination.AddPropertyManagementDestination,
    viewModel: AddPropertyViewModel = koinViewModel(),
    eventEmitter: EventEmitter<EdifikanaWindowDelegatedEvent> = koinInject(named(WindowIdentifier.DELEGATED_EVENT_BUS)),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize(destination.orgId)
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            AddPropertyEvent.Noop -> Unit
        }
    }

    ObserveEventEmitterEvents(eventEmitter) { event ->
        println("AddPropertyScreen received windowViewModel event: $event")
        when (event) {
            is EdifikanaWindowDelegatedEvent.HandleReceivedImage -> {
                viewModel.handleReceivedImages(listOf(event.uri))
            }
            is EdifikanaWindowDelegatedEvent.HandleReceivedImages -> {
                viewModel.handleReceivedImages(event.uris)
            }
            else -> Unit
        }
    }

    // Render the screen
    AddPropertyContent(
        uiState,
        onBackSelected = { viewModel.navigateBack() },
        onAddPropertySelected = { propertyName, address, imageUrl ->
            viewModel.addProperty(propertyName, address, imageUrl)
        },
        onTriggerPhotoPicker = { viewModel.triggerPhotoPicker() },
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun AddPropertyContent(
    content: AddPropertyUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onAddPropertySelected: (propertyName: String, address: String, imageUrl: String?) -> Unit,
    onTriggerPhotoPicker: () -> Unit,
) {
    var propertyName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Local state for default icon selection
    val defaultIcon = remember {
        PropertyIconOptions.getDefaultOptions().find { it.id == "S_DEPA" }
            ?: PropertyIconOptions.getDefaultOptions().first()
    }
    var localSelectedIcon by remember { mutableStateOf(defaultIcon) }

    // Effective selected icon: uploaded image takes priority over local selection
    val effectiveSelectedIcon = content.selectedIcon ?: localSelectedIcon

    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "Add Property", // TODO: Use string resource
                onNavigationIconSelected = onBackSelected,
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            fixedFooter = true,
            sectionContent = { sectionModifier ->
                EdifikanaTextField(
                    value = propertyName,
                    onValueChange = { propertyName = it },
                    label = "Property Name",
                    placeholder = "Enter Property Name",
                    modifier = sectionModifier,
                )
                EdifikanaTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Address",
                    placeholder = "Enter the property address",
                    modifier = sectionModifier,
                )
                EdifikanaImageSelector(
                    label = "Property Icon",
                    options = PropertyIconOptions.getOptionsWithUpload(),
                    selectedOption = effectiveSelectedIcon,
                    onOptionSelected = { option ->
                        if (option.id == "custom_upload") {
                            onTriggerPhotoPicker()
                        } else {
                            localSelectedIcon = option
                        }
                    },
                    placeholder = if (content.isUploading) "Uploading..." else "Select a property icon",
                    modifier = sectionModifier,
                )
            },
            buttonContent = { buttonModifier ->
                EdifikanaPrimaryButton(
                    text = stringResource(Res.string.text_add),
                    modifier = buttonModifier,
                    enabled = !content.isUploading,
                    onClick = {
                        val imageUrl = PropertyIconOptions.toImageUrl(effectiveSelectedIcon)
                        onAddPropertySelected(propertyName, address, imageUrl)
                    },
                )
            },
            overlay = {
                LoadingAnimationOverlay(content.isLoading || content.isUploading)
            }
        )
    }
}
