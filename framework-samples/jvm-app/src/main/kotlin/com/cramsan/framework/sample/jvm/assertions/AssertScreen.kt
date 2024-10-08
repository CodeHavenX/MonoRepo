package com.cramsan.framework.sample.jvm.assertions

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import org.koin.compose.koinInject

/**
 * Screen for testing assertions.
 */
@Composable
fun AssertScreen(
    assertViewModel: AssertViewModel = koinInject(),
) {
    AssertScreenContent(assertViewModel)
}

@Composable
private fun AssertScreenContent(
    eventHandler: AssertScreenEventHandler,
) {
    LazyColumn {
        item {
            Button(onClick = { eventHandler.tryAssert() }) {
                Text(text = "Try Assert")
            }
            Button(onClick = { eventHandler.tryAssertFalse() }) {
                Text(text = "Try tryAssertFalse")
            }
            Button(onClick = { eventHandler.tryAssertNull() }) {
                Text(text = "Try tryAssertNull")
            }
            Button(onClick = { eventHandler.tryAssertNotNull() }) {
                Text(text = "Try tryAssertNotNull")
            }
            Button(onClick = { eventHandler.tryAssertFailure() }) {
                Text(text = "Try tryAssertFailure")
            }
        }
    }
}

/**
 * Event handler for the AssertScreen.
 */
interface AssertScreenEventHandler {
    /**
     * Try an assert.
     */
    fun tryAssert()

    /**
     * Try an assert that should fail.
     */
    fun tryAssertFalse()

    /**
     * Try an assert that fails due to null.
     */
    fun tryAssertNull()

    /**
     * Try an assert that fails due to not null.
     */
    fun tryAssertNotNull()

    /**
     * Try an assert that fails due to an assertion failure.
     */
    fun tryAssertFailure()
}

@Preview
@Composable
private fun AssertScreenPreview() {
    MaterialTheme {
        AssertScreenContent(
            eventHandler = object : AssertScreenEventHandler {
                override fun tryAssert() = Unit
                override fun tryAssertFalse() = Unit
                override fun tryAssertNull() = Unit
                override fun tryAssertNotNull() = Unit
                override fun tryAssertFailure() = Unit
            },
        )
    }
}
