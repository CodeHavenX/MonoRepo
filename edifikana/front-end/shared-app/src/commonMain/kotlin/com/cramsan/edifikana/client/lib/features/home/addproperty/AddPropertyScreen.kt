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
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTextField
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.text_add
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

    // Render the screen
    AddPropertyContent(
        uiState,
        onBackSelected = { viewModel.navigateBack() },
        onAddPropertySelected = { propertyName, address ->
            viewModel.addProperty(propertyName, address)
        },
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
    onAddPropertySelected: (propertyName: String, address: String) -> Unit,
) {
    var propertyName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

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
            },
            buttonContent = { buttonModifier ->
                EdifikanaPrimaryButton(
                    text = stringResource(Res.string.text_add),
                    modifier = buttonModifier,
                    onClick = {
                        onAddPropertySelected(propertyName, address)
                    },
                )
            },
            overlay = {
                LoadingAnimationOverlay(content.isLoading)
            }
        )
    }
}
