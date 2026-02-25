package com.cramsan.edifikana.client.lib.features.home.propertydetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.ui.ObserveEventEmitterEvents
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
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

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize(destination.propertyId)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            PropertyDetailEvent.Noop -> Unit
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
        onImageUrlChanged = { viewModel.onImageUrlChanged(it) },
        onTriggerPhotoPicker = { viewModel.triggerPhotoPicker() },
    )
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
    onImageUrlChanged: (String?) -> Unit,
    onTriggerPhotoPicker: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = if (content.isEditMode) "Edit Property" else "Property Details",
                onNavigationIconSelected = onBackSelected,
                content = {
                    if (!content.isEditMode) {
                        IconButton(onClick = onEditSelected) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit property",
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete property",
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
                        Text("Property Icon", fontWeight = FontWeight.Bold)
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
                            label = "Property Name",
                        )
                    } else {
                        Text("Property Name", fontWeight = FontWeight.Bold)
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
                            label = "Address",
                        )
                    } else {
                        Text("Address", fontWeight = FontWeight.Bold)
                        Text(
                            text = content.address,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                }
                // Property Icon Selector - Show in edit mode
                if (content.isEditMode) {
                    Column(sectionModifier) {
                        // Determine current selected icon:
                        // 1. If custom image uploaded (selectedIcon from state), use that
                        // 2. Otherwise, parse from imageUrl
                        val currentIcon = content.selectedIcon
                            ?: PropertyIconOptions.fromImageUrl(content.imageUrl)
                            ?: PropertyIconOptions.getDefaultOptions().find { it.id == "S_DEPA" }

                        EdifikanaImageSelector(
                            label = "Property Icon",
                            options = PropertyIconOptions.getOptionsWithUpload(),
                            selectedOption = currentIcon,
                            onOptionSelected = { option ->
                                if (option.id == "custom_upload") {
                                    onTriggerPhotoPicker()
                                } else {
                                    onImageUrlChanged(PropertyIconOptions.toImageUrl(option))
                                }
                            },
                            placeholder = if (content.isUploading) "Uploading..." else "Select a property icon",
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
                        text = "Save",
                        modifier = buttonModifier,
                        enabled = !content.isUploading,
                        onClick = onSaveProperty,
                    )
                    EdifikanaSecondaryButton(
                        text = "Cancel",
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
            title = { Text("Delete Property") },
            text = { Text("Are you sure you want to delete this property? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteProperty()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
