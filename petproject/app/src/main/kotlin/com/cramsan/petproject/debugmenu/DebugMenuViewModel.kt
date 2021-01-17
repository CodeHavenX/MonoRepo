package com.cramsan.petproject.debugmenu

import android.app.Activity
import android.app.Application
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import com.cramsan.framework.core.BaseViewModel
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logI
import com.cramsan.petproject.appcore.provider.ModelProviderInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DebugMenuViewModel @ViewModelInject constructor(
    application: Application,
    dispatcherProvider: DispatcherProvider,
    val modelProvider: ModelProviderInterface,
    @Assisted savedStateHandle: SavedStateHandle
) : BaseViewModel(application, dispatcherProvider, savedStateHandle) {

    override val logTag: String
        get() = "DebugMenuViewModel"

    fun cleanCache() {
        logI(logTag, "ClearCache")
        GlobalScope.launch(Dispatchers.IO) {
            modelProvider.deleteAll()
        }
    }

    fun killApp(activity: Activity) {
        logI(logTag, "KillApp")
        activity.finishAffinity()
    }
}
