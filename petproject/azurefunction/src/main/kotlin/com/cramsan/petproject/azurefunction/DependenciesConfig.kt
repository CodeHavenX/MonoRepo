package com.cramsan.petproject.azurefunction

import com.cramsan.framework.assert.implementation.AssertUtil
import com.cramsan.framework.halt.implementation.HaltUtil
import com.cramsan.framework.halt.implementation.HaltUtilJVM
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLogger
import com.cramsan.framework.logging.implementation.LoggerJVM
import com.cramsan.framework.metrics.MetricsInterface
import com.cramsan.framework.metrics.implementation.Metrics
import com.cramsan.framework.metrics.implementation.MetricsErrorCallback
import com.cramsan.framework.thread.implementation.ThreadUtil
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import com.cramsan.petproject.appcore.storage.implementation.ModelStorage
import com.cramsan.petproject.appcore.storage.implementation.ModelStorageJdbcProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class  DependenciesConfig {
    val metrics by lazy {
        Metrics(object : MetricsInterface {
            override fun initialize() {}
            override fun log(tag: String, event: String) {}
            override fun log(tag: String, event: String, metadata: Map<String, String>) {}
        })
    }

    val errorCallback by lazy {
        MetricsErrorCallback(metrics)
    }

    val eventLogger by lazy {
        EventLogger(Severity.INFO, errorCallback, LoggerJVM())
    }

    val haltUtil by lazy {
        HaltUtil(HaltUtilJVM())
    }

    val assertUtil by lazy {
        AssertUtil(false, eventLogger, haltUtil)
    }

    val threadUtil by lazy {
        ThreadUtil(ThreadUtilJVM(eventLogger, assertUtil))
    }

    val modelStorage by lazy {
        val inputStream: InputStream? = javaClass
            .classLoader.getResourceAsStream("PetProject.sql")

        val tempFile = File.createTempFile("hello", ".tmp")
        println("Temp file On Default Location: " + tempFile.absolutePath)

        if (inputStream == null) {
            throw UnsupportedOperationException("Could not get InputStream")
        }

        val buffer = ByteArray(1024)
        val outStream: OutputStream = FileOutputStream(tempFile)

        var len: Int = inputStream.read(buffer)
        while (len != -1) {
            outStream.write(buffer, 0, len)
            outStream.flush()
            len = inputStream.read(buffer)
        }

        outStream.close()
        inputStream.close()

        eventLogger.log(Severity.ERROR, "TEST", tempFile.absolutePath)

        val dbPath: String? = tempFile.absolutePath

        if (dbPath == null) {
            throw UnsupportedOperationException("Path for sqlite is null")
        }
        val modelStorageDAO = ModelStorageJdbcProvider(
            dbPath
        ).provide()
        ModelStorage(modelStorageDAO,
            eventLogger,
            threadUtil)
    }
}
