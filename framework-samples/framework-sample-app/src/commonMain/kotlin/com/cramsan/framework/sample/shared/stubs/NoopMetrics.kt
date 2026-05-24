package com.cramsan.framework.sample.shared.stubs

import com.cramsan.framework.metrics.MetricNamespace
import com.cramsan.framework.metrics.MetricType
import com.cramsan.framework.metrics.MetricUnit
import com.cramsan.framework.metrics.MetricsInterface

/** No-op implementation of [MetricsInterface] for sample and testing use. */
class NoopMetrics : MetricsInterface {
    override fun initialize() = Unit

    override fun record(
        type: MetricType,
        namespace: MetricNamespace,
        tag: String,
        metadata: Map<String, String>?,
        value: Double,
        unit: MetricUnit,
    ) = Unit
}
