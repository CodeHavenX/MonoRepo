package com.cramsan.flyerboard.server.service

import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.server.datastore.FlyerDatastore
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

/**
 * Background scheduler that transitions expired approved flyers to [ARCHIVED][FlyerStatus.ARCHIVED].
 *
 * Call [start] once during application boot, passing the Ktor [Application][io.ktor.server.application.Application]
 * (which is a [CoroutineScope] tied to the server lifecycle). The launched coroutine will be
 * cancelled automatically when the server shuts down.
 */
@OptIn(ExperimentalTime::class)
class ExpiryService(
    private val flyerDatastore: FlyerDatastore,
) {

    /**
     * Starts the hourly expiry loop inside [scope].
     *
     * The loop runs once per hour. On each tick it queries for approved flyers whose
     * `expires_at` is in the past and updates each one to [ARCHIVED][FlyerStatus.ARCHIVED].
     */
    fun start(scope: CoroutineScope) {
        logI(TAG, "Starting expiry scheduler")
        scope.launch {
            while (true) {
                delay(1.hours)
                archiveExpiredFlyers()
            }
        }
    }

    private suspend fun archiveExpiredFlyers() {
        logD(TAG, "Checking for expired flyers")
        val now = Clock.System.now()
        val expired = flyerDatastore.listExpiredFlyers(now)
            .getOrElse { e ->
                logE(TAG, "Failed to list expired flyers", e)
                return
            }

        if (expired.isEmpty()) {
            logD(TAG, "No expired flyers found")
            return
        }

        logI(TAG, "Archiving ${expired.size} expired flyer(s)")
        for (flyer in expired) {
            flyerDatastore.updateFlyer(
                id = flyer.id,
                title = null,
                description = null,
                filePath = null,
                status = FlyerStatus.ARCHIVED,
                expiresAt = null,
            ).onFailure { e ->
                logE(TAG, "Failed to archive flyer ${flyer.id}", e)
            }.onSuccess {
                logD(TAG, "Archived flyer: %s", flyer.id)
            }
        }
    }

    companion object {
        private const val TAG = "ExpiryService"
    }
}
