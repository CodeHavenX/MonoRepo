package com.cramsan.framework.core.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Apply the given [block] to this [Modifier] if the nullable [condition] is true.
 *
 * Please note that the condition is nullable, so it can be used to represent a tri-state condition.
 * Only a true condition will apply the block.
 */
@Composable
fun Modifier.ifTrue(condition: Boolean?, block: @Composable Modifier.() -> Modifier): Modifier {
    return if (condition == true) {
        block(this)
    } else {
        this
    }
}

/**
 * Apply the given [block] to this [Modifier] if the [condition] is not true.
 *
 * Please note that the condition is nullable, so it can be used to represent a tri-state condition.
 * Both a null and false condition will apply the block.
 */
@Composable
fun Modifier.ifNotTrue(condition: Boolean?, block: @Composable Modifier.() -> Modifier): Modifier {
    return ifTrue(condition?.not(), block)
}
