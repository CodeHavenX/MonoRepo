package com.cramsan.framework.sample.jvm

import com.cramsan.framework.assertlib.AssertUtil
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Initializes the framework.
 */
class FrameworkInitializer : KoinComponent {

    /**
     * Initializes the framework.
     */
    fun initialize() {
        AssertUtil.setInstance(get())
    }
}
