package com.cramsan.petproject.download

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.cramsan.framework.core.BaseViewModel
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logI
import com.cramsan.petproject.appcore.provider.ModelProviderEventListenerInterface
import com.cramsan.petproject.appcore.provider.ModelProviderInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel that manages the [DownloadCatalogDialogFragment].
 */
@HiltViewModel
class DownloadCatalogViewModel @Inject constructor(
    application: Application,
    private val modelProvider: ModelProviderInterface,
    private val ioDispatcher: CoroutineDispatcher,
    dispatcherProvider: DispatcherProvider,
    savedStateHandle: SavedStateHandle,
) :
    BaseViewModel(application, dispatcherProvider, savedStateHandle),
    ModelProviderEventListenerInterface {

    override val logTag: String
        get() = "DownloadCatalogViewModel"

    private val mutableIsDownloadComplete = MutableLiveData<Boolean>()

    /**
     * Observable that emits an event when the catalog has been downloaded.
     */
    val observableIsDownloadComplete: LiveData<Boolean> = mutableIsDownloadComplete

    init {
        modelProvider.registerForCatalogEvents(this)
        mutableIsDownloadComplete.value = false
    }

    override fun onCleared() {
        super.onCleared()
        modelProvider.deregisterForCatalogEvents(this)
    }

    /**
     * Return true if the catalog is downloaded and valid. False otherwise.
     */
    fun isCatalogReady(): Boolean {
        logI("DownloadCatalogViewModel", "isCatalogReady")
        val unixTime = System.currentTimeMillis()
        val isReady = modelProvider.isCatalogAvailable(unixTime)
        mutableIsDownloadComplete.value = isReady
        return isReady
    }

    /**
     * Function that will try to download the catalog if needed.
     */
    fun downloadCatalog() {
        logI("DownloadCatalogViewModel", "downloadCatalog")
        val unixTime = System.currentTimeMillis()
        if (modelProvider.isCatalogAvailable(unixTime)) {
            mutableIsDownloadComplete.value = true
            return
        }
        mutableIsDownloadComplete.value = false
        ioScope.launch {
            downloadCatalogOnBackground()
        }
    }

    private suspend fun downloadCatalogOnBackground() = withContext(ioDispatcher) {
        val unixTime = System.currentTimeMillis()
        ioScope.launch {
            modelProvider.downloadCatalog(unixTime)
        }.join()
        mutableIsDownloadComplete.postValue(true)
    }

    override fun onCatalogUpdate(isReady: Boolean) {
        ioScope.launch {
            mutableIsDownloadComplete.postValue(!isReady)
        }
    }
}
