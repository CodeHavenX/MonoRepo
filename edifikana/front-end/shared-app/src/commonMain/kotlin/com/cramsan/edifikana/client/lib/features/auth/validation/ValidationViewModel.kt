package com.cramsan.edifikana.client.lib.features.auth.validation

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies

/**
 * ViewModel for the Validation screen.
 **/
class ValidationViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<ValidationEvent, ValidationUIState>(
    dependencies,
    ValidationUIState.Initial,
    TAG,
) {
    companion object {
        private const val TAG = "ValidationViewModel"
    }
}
