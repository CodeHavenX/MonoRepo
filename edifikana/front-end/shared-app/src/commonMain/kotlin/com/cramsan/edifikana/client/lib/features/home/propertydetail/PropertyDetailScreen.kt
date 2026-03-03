package com.cramsan.edifikana.client.lib.features.home.propertydetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.architecture.client.di.koinEventEmitter
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.home.shared.PropertyIconOptions
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowDelegatedEvent
import com.cramsan.edifikana.client.ui.components.EdifikanaImage
import com.cramsan.edifikana.client.ui.components.EdifikanaImageSelector
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaSecondaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTextField
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.components.ImageSelectorBottomsheet
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.rememberDialogController
import com.cramsan.framework.core.compose.ui.ObserveEventEmitterEvents
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.add_property_screen_address_label
import edifikana_lib.add_property_screen_property_icon_label
import edifikana_lib.add_property_screen_property_icon_placeholder
import edifikana_lib.add_property_screen_property_name_label
import edifikana_lib.edifikana_string_cancel
import edifikana_lib.edifikana_string_delete
import edifikana_lib.edifikana_string_save
import edifikana_lib.property_detail_screen_delete_dialog_message
import edifikana_lib.property_detail_screen_delete_label
import edifikana_lib.property_detail_screen_edit_title
import edifikana_lib.property_detail_screen_title
import edifikana_lib.text_upload
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * PropertyDetail screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun PropertyDetailScreen(
    destination: HomeDestination.PropertyManagementDestination,
    viewModel: PropertyDetailViewModel = koinViewModel(),
    eventEmitter: EventEmitter<EdifikanaWindowDelegatedEvent> = koinEventEmitter(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val dialogController = rememberDialogController()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize(destination.propertyId)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            PropertyDetailEvent.OpenImageSelector -> {
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
    PropertyDetailContent(
        uiState,
        onBackSelected = { viewModel.navigateBack() },
        onEditSelected = { viewModel.toggleEditMode() },
        onCancelEdit = { viewModel.cancelEdit() },
        onSaveProperty = { viewModel.saveProperty() },
        onDeleteProperty = { viewModel.deleteProperty() },
        onNameChanged = { viewModel.onNameChanged(it) },
        onAddressChanged = { viewModel.onAddressChanged(it) },
        onOpenSelectorSelected = { viewModel.openImageSelector() },
    )

    dialogController.Render()
}

/**
 * Content of the PropertyDetail screen.
 */
@Composable
internal fun PropertyDetailContent(
    content: PropertyDetailUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onEditSelected: () -> Unit,
    onCancelEdit: () -> Unit,
    onSaveProperty: () -> Unit,
    onDeleteProperty: () -> Unit,
    onNameChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onOpenSelectorSelected: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = if (content.isEditMode) stringResource(Res.string.property_detail_screen_edit_title) else stringResource(Res.string.property_detail_screen_title),
                onNavigationIconSelected = onBackSelected,
                content = {
                    if (!content.isEditMode) {
                        IconButton(onClick = onEditSelected) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(Res.string.property_detail_screen_edit_title),
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(Res.string.property_detail_screen_delete_label),
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            fixedFooter = content.isEditMode,
            sectionContent = { sectionModifier ->
                // Property Icon - Show at the top
                if (!content.isEditMode) {
                    Column(
                        modifier = sectionModifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(stringResource(Res.string.add_property_screen_property_icon_label), fontWeight = FontWeight.Bold)
                        val iconOption = PropertyIconOptions.fromImageUrl(content.imageUrl)
                            ?: PropertyIconOptions.getDefaultOptions().find { it.id == "S_DEPA" }
                        iconOption?.let { option ->
                            EdifikanaImage(
                                imageSource = option.imageSource,
                                contentDescription = option.displayName,
                                cornerRadius = 8.dp,
                                size = 80.dp,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                    }
                }
                Column(sectionModifier) {
                    if (content.isEditMode) {
                        EdifikanaTextField(
                            value = content.name,
                            onValueChange = onNameChanged,
                            label = stringResource(Res.string.add_property_screen_property_name_label),
                        )
                    } else {
                        Text(stringResource(Res.string.add_property_screen_property_name_label), fontWeight = FontWeight.Bold)
                        Text(
                            text = content.name,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                }
                Column(sectionModifier) {
                    if (content.isEditMode) {
                        EdifikanaTextField(
                            value = content.address,
                            onValueChange = onAddressChanged,
                            label = stringResource(Res.string.add_property_screen_address_label),
                        )
                    } else {
                        Text(stringResource(Res.string.add_property_screen_address_label), fontWeight = FontWeight.Bold)
                        Text(
                            text = content.address,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                }
                // Property Icon Selector - Show in edit mode
                if (content.isEditMode) {
                    Column(sectionModifier) {
                        EdifikanaImageSelector(
                            label = stringResource(Res.string.add_property_screen_property_icon_label),
                            selectedOption = content.selectedIcon,
                            onOpenSelectorSelected = onOpenSelectorSelected,
                            placeholder = if (content.isUploading) stringResource(Res.string.text_upload) else stringResource(Res.string.add_property_screen_property_icon_placeholder),
                        )

                        // Show upload error if any
                        content.uploadError?.let { error ->
                            Text(
                                text = error,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
            },
            buttonContent = if (content.isEditMode) {
                { buttonModifier ->
                    EdifikanaPrimaryButton(
                        text = stringResource(Res.string.edifikana_string_save),
                        modifier = buttonModifier,
                        enabled = !content.isUploading,
                        onClick = onSaveProperty,
                    )
                    EdifikanaSecondaryButton(
                        text = stringResource(Res.string.edifikana_string_cancel),
                        modifier = buttonModifier,
                        onClick = onCancelEdit,
                    )
                }
            } else {
                null
            },
            overlay = {
                LoadingAnimationOverlay(content.isLoading || content.isUploading)
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.property_detail_screen_delete_label)) },
            text = { Text(stringResource(Res.string.property_detail_screen_delete_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteProperty()
                    }
                ) {
                    Text(stringResource(Res.string.edifikana_string_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(Res.string.edifikana_string_cancel))
                }
            }
        )
    }
}
