package com.cramsan.framework.halt.implementation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.R
import java.util.concurrent.atomic.AtomicBoolean

/**
 * [HaltUtilDelegate] implementation for Android. On debug builds this halts the calling thread and
 * surfaces a notification with the stack trace; on release builds it is a no-op. AGP's Kotlin
 * Multiplatform library plugin has no debug/release source set variants, so [isDebugBuild] must be
 * supplied by the caller instead of being resolved from a variant-specific implementation.
 */
class HaltUtilAndroid(private val appContext: Context, private val isDebugBuild: Boolean) : HaltUtilDelegate {
    private val shouldStop = AtomicBoolean(false)

    override fun stopThread() {
        if (!isDebugBuild) return

        shouldStop.set(true)

        displayNotification()

        while (shouldStop.get()) {
            Thread.sleep(SLEEP_TIME)
        }
    }

    override fun resumeThread() {
        shouldStop.set(false)
    }

    override fun crashApp() {
        if (!isDebugBuild) return

        android.os.Process.killProcess(android.os.Process.myPid())
    }

    private fun getStacktrace() =
        Thread
            .currentThread()
            .stackTrace
            .drop(STACK_TRACE_HEAD_EXTRA_LINES)
            .joinToString(separator = "\n") { "at ${it.methodName}(${it.fileName}:${it.lineNumber})" }

    private fun displayNotification() {
        createNotificationChannel()
        val builder =
            NotificationCompat
                .Builder(appContext, CHANNEL_ID)
                .setContentTitle("Thread has been halted")
                .setSmallIcon(R.drawable.debug_icon)
                .setContentText(getStacktrace())
                .setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText(getStacktrace()),
                ).setPriority(NotificationCompat.PRIORITY_MAX)
        with(NotificationManagerCompat.from(appContext)) {
            // NotificationId is a unique int for each notification that you must define
            // Setting notification ID to MAX_VALUE as to reduce the risk
            // of collision with other actual notification ids.
            notify(Int.MAX_VALUE, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = appContext.getString(R.string.halt_util_channel_name)
            val descriptionText = appContext.getString(R.string.halt_util_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val STACK_TRACE_HEAD_EXTRA_LINES = 5
        private const val SLEEP_TIME = 1000L
        private const val CHANNEL_ID = "com.cramsan.framework.halt.implementation"
    }
}
