package com.cramsan.framework.sample.shared.features.main.metrics

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.metrics.MetricNamespace
import com.cramsan.framework.metrics.MetricType
import com.cramsan.framework.metrics.MetricsInterface
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import kotlinx.coroutines.launch

private object SampleNamespace : MetricNamespace {
    override val identifier: String = "sample"
}

/**
 * ViewModel for the Metrics screen.
 */
@FrontendViewModel
class MetricsViewModel(dependencies: ViewModelDependencies, private val metrics: MetricsInterface) :
    BaseViewModel<MetricsEvent, MetricsUIState>(
        dependencies,
        MetricsUIState.Initial,
        TAG,
    ) {
    /**
     * Call MetricsInterface.initialize().
     */
    fun initialize() {
        metrics.initialize()
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(lastAction = "initialize() called") }
        }
    }

    /**
     * Record a COUNT metric.
     */
    fun recordCount() {
        metrics.record(MetricType.COUNT, SampleNamespace, "sample_count_tag")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(lastAction = "record(COUNT, sample, sample_count_tag) called") }
        }
    }

    /**
     * Record a LATENCY metric.
     */
    fun recordLatency() {
        metrics.record(MetricType.LATENCY, SampleNamespace, "sample_latency_tag", value = 250.0)
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(lastAction = "record(LATENCY, sample, sample_latency_tag, 250ms) called") }
        }
    }

    /**
     * Record an EVENT metric.
     */
    fun recordEvent() {
        metrics.record(MetricType.EVENT, SampleNamespace, "sample_event_tag", metadata = mapOf("key" to "value"))
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(lastAction = "record(EVENT, sample, sample_event_tag, metadata) called") }
        }
    }

    /**
     * Record a SUCCESS metric.
     */
    fun recordSuccess() {
        metrics.record(MetricType.SUCCESS, SampleNamespace, "sample_success_tag")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(lastAction = "record(SUCCESS, sample, sample_success_tag) called") }
        }
    }

    /**
     * Record a FAILURE metric.
     */
    fun recordFailure() {
        metrics.record(MetricType.FAILURE, SampleNamespace, "sample_failure_tag")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(lastAction = "record(FAILURE, sample, sample_failure_tag) called") }
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
        private const val TAG = "MetricsViewModel"
    }
}
