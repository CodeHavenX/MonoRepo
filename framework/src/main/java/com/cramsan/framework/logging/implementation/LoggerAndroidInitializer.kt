package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.EventLoggerPlatformInitializerInterface

class LoggerAndroidInitializer(override val platformLogger: EventLoggerInterface
) : EventLoggerPlatformInitializerInterface