package ${PACKAGE_NAME}.${Package_Name}

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * A class representing navigating to the ${Feature_Name} screen.
 * TODO: Move this destination to the respective Route.
 * TODO: If the Destination has arguments, make this class into a `data class` add them arguments.
 */
data object ${Feature_Name}Destination : RouteDestination( // TODO: Update the Destination to match the respective Route.
    Route.${Feature_Name}.route, // TODO: Update to the respective route Enum
) {
    // TODO: If the this class is an `object class` remove the `companion object` block.
    companion object {
        /**
         * Create a [${Feature_Name}Destination] from a NavBackStackEntry.
         */
        fun unpack(backstackEntry: NavBackStackEntry): ${Feature_Name}Destination {
            return ${Feature_Name}Destination
        }
    }
}


/**
 * ${Feature_Name} screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
 // TODO: Register this screen as a new route within the appropriate router.
@Composable
fun ${Feature_Name}Screen(
    destination: ${Feature_Name}Destination, // TODO: If the destination is a data object, it can be removed as an argument.
    modifier: Modifier = Modifier,
    viewModel: ${Feature_Name}ViewModel = koinViewModel(),
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

    LaunchedEffect(Unit) {
        launch {
            viewModel.events.collect { event ->
                when (event) {
                    ${Feature_Name}Event.Noop -> Unit
                }
            }
        }
    }

    // Render the screen
    ${Feature_Name}Content(
        content = uiState,
        onBackSelected = { viewModel.onBackSelected() },
        modifier = modifier,
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun ${Feature_Name}Content(
    content: ${Feature_Name}UIState,
    onBackSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            // TODO: Update this function to the respective invocation.
            TopBar(
                title = content.title,
                onCloseClicked = onBackSelected,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            ScreenLayout(
                sectionContent = { sectionModifier ->
                },
                buttonContent = { buttonModifier ->
                }
            )
            LoadingAnimationOverlay(content.isLoading)
        }
    }
}
