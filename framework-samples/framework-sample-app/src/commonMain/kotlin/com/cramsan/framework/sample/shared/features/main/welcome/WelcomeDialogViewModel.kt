package com.cramsan.framework.sample.shared.features.main.welcome

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseResultViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.navigation.NavResultKey
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import kotlinx.coroutines.launch

/**
 * ViewModel for the WelcomeDialog screen.
 */
@FrontendViewModel
class WelcomeDialogViewModel(dependencies: ViewModelDependencies) :
    BaseResultViewModel<Nothing, WelcomeDialogUIState, ThemeSelection>(
        dependencies,
        WelcomeDialogUIState.Initial,
        TAG,
    ) {
    /**
     * Select a theme. This function does not do anything, it is used for testing the result API.
     */
    fun selectTheme(selection: ThemeSelection) {
        viewModelCoroutineScope.launch {
            emitWindowEvent(navigateBackFrom(selection))
        }
    }

    /**
     * Dismiss the dialog.
     */
    fun dismiss() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "WelcomeDialogViewModel"
        val resultKey = NavResultKey<ThemeSelection>(WelcomeDialogViewModel::class.simpleName!!)
    }
}
