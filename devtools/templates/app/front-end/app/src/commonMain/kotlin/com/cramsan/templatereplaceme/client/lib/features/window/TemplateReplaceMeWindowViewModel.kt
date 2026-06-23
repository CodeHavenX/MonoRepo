package com.cramsan.templatereplaceme.client.lib.features.window

import androidx.compose.material3.SnackbarResult
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.logI
import com.cramsan.templatereplaceme.client.lib.navigation.TemplateReplaceMePathNavigation
import kotlinx.coroutines.launch

/**
 * View model for the entire window.
 */
@FrontendViewModel
class TemplateReplaceMeWindowViewModel(
    dependencies: ViewModelDependencies,
    private val windowEventEmitter: EventEmitter<WindowEvent>,
    private val delegatedEvents: EventReceiver<TemplateReplaceMeWindowDelegatedEvent>,
) : BaseViewModel<TemplateReplaceMeWindowViewModelEvent, TemplateReplaceMeWindowUIState>(
    dependencies,
    TemplateReplaceMeWindowUIState,
    TAG,
) {
    init {
        viewModelCoroutineScope.launch {
            windowEventEmitter.events.collect { event ->
                logI(TAG, "Window event received: $event")
                emitEvent(
                    TemplateReplaceMeWindowViewModelEvent.TemplateReplaceMeWindowEventWrapper(
                        event as TemplateReplaceMeWindowsEvent,
                    ),
                )
            }
        }
    }

    /**
     * Resolves [rawUrl] and navigates to the matching destination, if any. No-op for
     * unrecognized URLs.
     *
     * Works unchanged for a native custom-scheme deep link (e.g. an OAuth/auth-provider callback
     * like `templatereplaceme://host/auth/callback`) as long as the redirect URL embeds the
     * destination's `@WebPath` as its path — [TemplateReplaceMePathNavigation] strips the
     * scheme/authority generically (see `WebRoute.fromWebPath`), so no per-link alias table is
     * needed here.
     */
    fun handleDeepLink(rawUrl: String) {
        viewModelCoroutineScope.launch {
            val destination = TemplateReplaceMePathNavigation.pathToDestination(rawUrl) ?: return@launch
            logI(TAG, "Deep link resolved to: $destination")
            emitEvent(
                TemplateReplaceMeWindowViewModelEvent.TemplateReplaceMeWindowEventWrapper(
                    TemplateReplaceMeWindowsEvent.NavigateToScreen(destination),
                ),
            )
        }
    }

    /**
     * Handle snackbar result and emits it as a delegated event. Any observer can then consume this event.
     */
    fun handleSnackbarResult(result: SnackbarResult) {
        viewModelCoroutineScope.launch {
            logI(TAG, "Result from snackbar: $result")
            delegatedEvents.push(TemplateReplaceMeWindowDelegatedEvent.HandleSnackbarResult(result))
        }
    }

    companion object {
        private const val TAG = "TemplateReplaceMeWindowViewModel"
    }
}
