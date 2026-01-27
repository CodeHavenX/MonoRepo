package com.cramsan.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * This component represents a cell in a list. It will contain a start slot, content, and end slot.
 */
@Composable
fun ListCell(
    modifier: Modifier = Modifier,
    onSelection: (() -> Unit)?,
    startSlot: @Composable (() -> Unit)? = null,
    endSlot: @Composable (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Row(
        modifier = Modifier.then(
            if (onSelection != null) {
                Modifier.clickable { onSelection() }
            } else {
                Modifier
            },
        ).then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        if (startSlot != null) {
            startSlot()
        }
        Box(
            modifier = Modifier.weight(1f),
        ) {
            content()
        }
        if (endSlot != null) {
            endSlot()
        }
    }
}
