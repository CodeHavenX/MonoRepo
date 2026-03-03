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
import com.cramsan.architecture.client.di.koinEventEmitter
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.home.shared.PropertyIconOptions
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowDelegatedEvent
import com.cramsan.edifikana.client.ui.components.EdifikanaImageSelector
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTextField
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.components.ImageOptionUIModel
import com.cramsan.edifikana.client.ui.components.ImageSelectorBottomsheet
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.rememberDialogController
import com.cramsan.framework.core.compose.ui.ObserveEventEmitterEvents
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.add_property_screen_address_label
import edifikana_lib.add_property_screen_address_placeholder
import edifikana_lib.add_property_screen_property_icon_label
import edifikana_lib.add_property_screen_property_icon_placeholder
import edifikana_lib.add_property_screen_property_name_label
import edifikana_lib.add_property_screen_property_name_placeholder
import edifikana_lib.add_property_screen_title
import edifikana_lib.text_add
import edifikana_lib.text_upload
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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
    eventEmitter: EventEmitter<EdifikanaWindowDelegatedEvent> = koinEventEmitter(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val dialogController = rememberDialogController()

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
            AddPropertyEvent.OpenImageSelector -> {
                val modal = ImageSelectorBottomsheet(
                    label = "Select Property Icon",
                    options = PropertyIconOptions.getOptionsWithUpload(),
                    selectedOption = uiState.selectedIcon,
                    onOptionSelected = { option ->
                        viewModel.selectPhoto(option)
                    },
                )
                dialogController.showDialog(modal)
            }
        }
    }

    ObserveEventEmitterEvents(eventEmitter) { event ->
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
        onAddPropertySelected = { propertyName, address, selectedIcon ->
            viewModel.addProperty(propertyName, address, selectedIcon)
        },
        onOpenSelectorSelected = { viewModel.openImageSelector() },
    )

    dialogController.Render()
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun AddPropertyContent(
    content: AddPropertyUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onAddPropertySelected: (propertyName: String, address: String, selectedIcon: ImageOptionUIModel?) -> Unit,
    onOpenSelectorSelected: () -> Unit,
) {
    var propertyName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.add_property_screen_title),
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
                    label = stringResource(Res.string.add_property_screen_property_name_label),
                    placeholder = stringResource(Res.string.add_property_screen_property_name_placeholder),
                    modifier = sectionModifier,
                )
                EdifikanaTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = stringResource(Res.string.add_property_screen_address_label),
                    placeholder = stringResource(Res.string.add_property_screen_address_placeholder),
                    modifier = sectionModifier,
                )
                EdifikanaImageSelector(
                    label = "Property Icon",
                    selectedOption = content.selectedIcon,
                    placeholder = if (content.isUploading) {
                        stringResource(Res.string.text_upload)
                    } else {
                        stringResource(Res.string.add_property_screen_property_icon_placeholder)
                    },                    onOpenSelectorSelected = onOpenSelectorSelected,
                    modifier = sectionModifier,
                )
            },
            buttonContent = { buttonModifier ->
                EdifikanaPrimaryButton(
                    text = stringResource(Res.string.text_add),
                    modifier = buttonModifier,
                    enabled = !content.isUploading,
                    onClick = {
                        onAddPropertySelected(propertyName, address, content.selectedIcon)
                    },
                )
            },
            overlay = {
                LoadingAnimationOverlay(content.isLoading || content.isUploading)
            }
        )
    }
}
