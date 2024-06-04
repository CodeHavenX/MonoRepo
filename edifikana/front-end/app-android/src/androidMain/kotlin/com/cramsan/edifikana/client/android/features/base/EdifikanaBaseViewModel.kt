package com.cramsan.edifikana.client.android.features.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

@Suppress("UnnecessaryAbstractClass")
abstract class EdifikanaBaseViewModel(
    exceptionHandler: CoroutineExceptionHandler
) : ViewModel() {

    val viewModelScope: CoroutineScope = CoroutineScope(
        SupervisorJob() + exceptionHandler + Dispatchers.Main
    )

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
