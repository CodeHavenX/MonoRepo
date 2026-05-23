package com.cramsan.framework.sample.shared.features.main.remoteconfig

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.remoteconfig.RemoteConfig
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.sample.shared.stubs.SampleRemoteConfigPayload
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * ViewModel for the RemoteConfig screen.
 */
@FrontendViewModel
class RemoteConfigViewModel(
    dependencies: ViewModelDependencies,
    private val remoteConfig: RemoteConfig<SampleRemoteConfigPayload>,
) : BaseViewModel<RemoteConfigEvent, RemoteConfigUIState>(
    dependencies,
    RemoteConfigUIState.Initial,
    TAG,
) {
    /**
     * Check and display whether the config payload is ready.
     */
    fun checkIsPayloadReady() {
        val ready = remoteConfig.isConfigPayloadReady()
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isPayloadReady = ready, lastAction = "isConfigPayloadReady() → $ready") }
        }
    }

    /**
     * Download the config payload and update state on completion.
     */
    fun downloadPayload() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true, lastAction = "downloadConfigPayload() in progress…") }
            val success = remoteConfig.downloadConfigPayload()
            val ready = remoteConfig.isConfigPayloadReady()
            updateUiState {
                it.copy(
                    isLoading = false,
                    isPayloadReady = ready,
                    lastAction = "downloadConfigPayload() → success=$success",
                )
            }
        }
    }

    /**
     * Trigger a fire-and-forget async download.
     */
    fun downloadAsync() {
        remoteConfig.downloadConfigPayloadAsync()
        viewModelCoroutineScope.launch {
            val ready = remoteConfig.isConfigPayloadReady()
            updateUiState {
                it.copy(
                    isPayloadReady = ready,
                    lastAction = "downloadConfigPayloadAsync() called",
                )
            }
            repeat(MAX_RETRY) {
                delay(1.seconds)
                val ready = remoteConfig.isConfigPayloadReady()
                updateUiState {
                    it.copy(
                        isPayloadReady = ready,
                        lastAction = "isConfigPayloadReady() called",
                    )
                }
                if (ready) {
                    return@launch
                }
            }
        }
    }

    /**
     * Fetch the payload or null and display its contents.
     */
    fun getPayloadOrNull() {
        val payload = remoteConfig.getConfigPayloadOrNull()
        val info = payload?.let { "featureEnabled=${it.featureEnabled}, configValue=${it.configValue}" } ?: "null"
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(payloadInfo = info, lastAction = "getConfigPayloadOrNull() → $info") }
        }
    }

    /**
     * Fetch the payload or a default and display its contents.
     */
    fun getPayloadOrDefault() {
        val payload = remoteConfig.getConfigPayloadOrDefault()
        val info = "featureEnabled=${payload.featureEnabled}, configValue=${payload.configValue}"
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(payloadInfo = info, lastAction = "getConfigPayloadOrDefault() → $info") }
        }
    }

    /**
     * Navigate back to the main menu.
     */
    fun navigateBack() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateBack)
        }
    }

    companion object {
        private const val MAX_RETRY = 5
        private const val TAG = "RemoteConfigViewModel"
    }
}
