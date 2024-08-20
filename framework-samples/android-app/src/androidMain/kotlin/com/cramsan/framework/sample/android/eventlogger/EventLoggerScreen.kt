package com.cramsan.framework.sample.android.eventlogger

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.sample.android.theme.CoreProjectTheme

/**
 * Screen for the Event Logger.
 */
@Composable
fun EventLoggerScreen() {
    val eventLoggerViewModel: EventLoggerViewModel = hiltViewModel()

    EventLoggerScreenContent(eventLoggerViewModel)
}

@Composable
private fun EventLoggerScreenContent(
    eventHandler: EventLoggerScreenEventHandler,
) {
    Column {
        Text(text = "Set Severity")
        Row(
            modifier = Modifier.fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Severity.values().forEach {
                Button(onClick = { eventHandler.setSeverity(it) }) {
                    Text(text = it.name)
                }
            }
        }
        Text(text = "Log a message")
        Button(onClick = { eventHandler.tryLogV() }) {
            Text(text = "Try LogV")
        }
        Button(onClick = { eventHandler.tryLogD() }) {
            Text(text = "Try LogD")
        }
        Button(onClick = { eventHandler.tryLogI() }) {
            Text(text = "Try LogI")
        }
        Button(onClick = { eventHandler.tryLogW() }) {
            Text(text = "Try LogW")
        }
        Button(onClick = { eventHandler.tryLogE() }) {
            Text(text = "Try LogE")
        }
    }
}

/**
 * ViewModel for the EventLoggerScreen.
 */
interface EventLoggerScreenEventHandler {
    /**
     * Try to log a message with the specified severity.
     */
    fun tryLogV()

    /**
     * Try to log a message with the specified severity.
     */
    fun tryLogD()

    /**
     * Try to log a message with the specified severity.
     */
    fun tryLogI()

    /**
     * Try to log a message with the specified severity.
     */
    fun tryLogW()

    /**
     * Try to log a message with the specified severity.
     */
    fun tryLogE()

    /**
     * Set the severity for the next log message.
     */
    fun setSeverity(severity: Severity)
}

@Preview
@Composable
private fun AssertScreenPreview() {
    CoreProjectTheme {
        EventLoggerScreenContent(
            eventHandler = object : EventLoggerScreenEventHandler {
                override fun tryLogV() = Unit
                override fun tryLogD() = Unit
                override fun tryLogI() = Unit
                override fun tryLogW() = Unit
                override fun tryLogE() = Unit
                override fun setSeverity(severity: Severity) = Unit
            }
        )
    }
}
