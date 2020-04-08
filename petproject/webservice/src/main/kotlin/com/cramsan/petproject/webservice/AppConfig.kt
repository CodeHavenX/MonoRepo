package com.cramsan.petproject.webservice

import com.cramsan.framework.assert.AssertUtilInterface
import com.cramsan.framework.assert.implementation.AssertUtil
import com.cramsan.framework.halt.HaltUtilInterface
import com.cramsan.framework.halt.implementation.HaltUtil
import com.cramsan.framework.halt.implementation.HaltUtilJVM
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLogger
import com.cramsan.framework.logging.implementation.LoggerJVM
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtil
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import com.cramsan.petproject.appcore.storage.implementation.ModelStorage
import com.cramsan.petproject.appcore.storage.ModelStorageInterface
import com.cramsan.petproject.appcore.storage.implementation.ModelStorageJdbcProvider

@Configuration
class AppConfig {

    @Bean
    fun eventLogger(): EventLoggerInterface {
        return EventLogger(Severity.INFO, LoggerJVM())
    }

    @Bean
    fun haltUtil(): HaltUtilInterface {
        return HaltUtil(HaltUtilJVM())
    }

    @Bean
    fun assertUtil(): AssertUtilInterface {
        return AssertUtil(false, eventLogger(), haltUtil())
    }

    @Bean
    fun threadUtil(): ThreadUtilInterface {
        return ThreadUtil(ThreadUtilJVM(eventLogger(), assertUtil()))
    }

    @Bean
    fun modelStorage(): ModelStorageInterface {
        val resource = ClassPathResource("PetProject.sql")
        if (!resource.isFile || !resource.exists()) {
            throw UnsupportedOperationException("File not found")
        }
        val dbPath: String? = resource.uri.path
        if (dbPath == null) {
            throw UnsupportedOperationException("Path for sqlite is null")
        }
        val modelStorageDAO = ModelStorageJdbcProvider(
            dbPath
        ).provide()
        return ModelStorage(modelStorageDAO,
            eventLogger(),
            threadUtil())
    }
}
