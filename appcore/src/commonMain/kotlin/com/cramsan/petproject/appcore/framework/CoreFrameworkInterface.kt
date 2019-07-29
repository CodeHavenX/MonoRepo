package com.cramsan.petproject.appcore.framework

import com.cramsan.framework.halt.HaltUtilInterface
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.petproject.appcore.provider.ModelProviderInterface
import com.cramsan.petproject.appcore.provider.implementation.ModelProviderPlatformInitializer
import com.cramsan.petproject.appcore.storage.ModelStorageInterface
import com.cramsan.petproject.appcore.storage.implementation.ModelStoragePlatformInitializer

interface CoreFrameworkInterface {

    // Initialize all Framework(low-level) components
    val eventLogger: EventLoggerInterface
    fun initEventLogger(targetSeverity: Severity)

    val threadUtil: ThreadUtilInterface
    fun initThreadUtil()

    val haltUtil: HaltUtilInterface
    fun initHaltUtil()

    // Initialize all Core(mid-level) components
    val modelStorage: ModelStorageInterface
    fun initModelStorage(platformInitializer: ModelStoragePlatformInitializer)

    val modelProvider: ModelProviderInterface
    fun initModelProvider(platformInitializer: ModelProviderPlatformInitializer)
}