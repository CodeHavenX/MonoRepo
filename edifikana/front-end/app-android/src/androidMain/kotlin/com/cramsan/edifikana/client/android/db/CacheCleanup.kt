package com.cramsan.edifikana.client.android.db

import android.util.Log
import androidx.core.app.ComponentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import java.io.File
import kotlin.io.path.getLastModifiedTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

// TODO: Deprecate this function since now we are using the Media API and files can be removed manually.
// Additionally we also provide a mechanism to identify situations of low storage.
fun ComponentActivity.clearOldFiles(
    ttl: Duration = 15.days,
    clock: Clock = Clock.System,
) {
    GlobalScope.launch(Dispatchers.IO) {
        val currentTime = clock.now()
        externalMediaDirs.forEach {
            clearOldFilesImp(it, ttl, currentTime)
        }
    }
}

private fun clearOldFilesImp(
    file: File,
    ttl: Duration,
    currentTime: Instant,
) {
    if (file.isFile) {
        val lastModifiedTime = file.toPath().getLastModifiedTime()
        if ((currentTime - lastModifiedTime.toInstant().toKotlinInstant()) > ttl) {
            Log.w("clearOldFiles", "File ${file.name} is being cleared")
            file.delete()
        }
    } else {
        file.listFiles()?.forEach {
            clearOldFilesImp(it, ttl, currentTime)
        }
    }
}
