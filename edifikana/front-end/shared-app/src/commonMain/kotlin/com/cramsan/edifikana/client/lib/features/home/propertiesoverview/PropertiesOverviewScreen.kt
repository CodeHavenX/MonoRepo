package com.cramsan.edifikana.client.lib.features.home.propertiesoverview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.FlipCameraAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil3.compose.AsyncImage
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.theme.Size
import edifikana_lib.Res
import edifikana_lib.text_flip_camera
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel



/**
 * PropertiesOverview screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun PropertiesOverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: PropertiesOverviewViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            PropertiesOverviewEvent.Noop -> Unit
        }
    }

    // Render the screen
    PropertiesOverviewContent(
        content = uiState,
        modifier = modifier,
    )
}

/**
 * Content of the PropertiesOverview screen.
 */
@Composable
internal fun PropertiesOverviewContent(
    content: PropertiesOverviewUIState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {},
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            ScreenLayout(
                sectionContent = { sectionModifier ->
                    content.propertyList.forEach {
                        PropertyItem(
                            property = it,
                            modifier = sectionModifier,
                        )
                    }
                },
            )
            LoadingAnimationOverlay(content.isLoading)
        }
    }
}

@Composable
private fun PropertyItem(
    property: PropertyItemUIModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = MaterialTheme.shapes.medium,
            ),
    ) {
        val imageModifier = Modifier
            .size(Size.medium)
        if (property.imageUrl != null) {
            AsyncImage(
                model = property.imageUrl,
                contentDescription = null,
                modifier = imageModifier,
            )
        } else {
            Icon(
                imageVector = Icons.Sharp.FlipCameraAndroid,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = imageModifier,
            )
        }
        //Spacer(Modifier.weight(1f))
        Column {
            Text(property.name)
            Text(property.address)
        }
    }
}