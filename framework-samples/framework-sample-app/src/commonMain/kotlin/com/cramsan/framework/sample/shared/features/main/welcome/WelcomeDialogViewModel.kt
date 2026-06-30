package com.cramsan.framework.sample.shared.features.main.welcome

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.sample.shared.features.main.MainDestination
import com.cramsan.framework.sample.shared.features.navigateBackWith
import kotlinx.coroutines.launch

/**
 * ViewModel for the WelcomeDialog screen.
 */
@FrontendViewModel
class WelcomeDialogViewModel(dependencies: ViewModelDependencies) :
    BaseViewModel<Nothing, WelcomeDialogUIState>(dependencies, WelcomeDialogUIState.Initial, TAG) {

    fun selectTheme(selection: ThemeSelection) {
        viewModelCoroutineScope.launch {
            emitWindowEvent(MainDestination.WelcomeDialogDestination.themeResult.navigateBackWith(selection))
        }
    }

    fun dismiss() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "WelcomeDialogViewModel"
    }
}
