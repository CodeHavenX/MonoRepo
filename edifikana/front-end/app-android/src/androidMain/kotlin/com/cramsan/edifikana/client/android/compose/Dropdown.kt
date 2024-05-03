package com.cramsan.edifikana.client.android.compose

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun <T> Dropdown(
    label: String,
    modifier: Modifier,
    items: List<T>,
    itemLabels: List<String>,
    startValueMatcher: ((T) -> Boolean)? = null,
    valueSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember {
        val startIndex = startValueMatcher?.let {
            items.indexOfFirst(startValueMatcher)
        } ?: 0
        mutableIntStateOf(startIndex)
    }
    Box(
        modifier = modifier,
    ) {
        val focusRequester = FocusRequester()
        val focusManager = LocalFocusManager.current

        TextField(
            value = itemLabels[selectedIndex],
            modifier = Modifier
                .fillMaxWidth()
                .focusable()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        expanded = true
                    }
                }
                .focusRequester(focusRequester),
            trailingIcon = {
                val icon = if (expanded) {
                    Icons.Filled.ArrowDropUp
                } else {
                    Icons.Filled.ArrowDropDown
                }
                Icon(icon, "")
            },
            singleLine = true,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                focusRequester.freeFocus()
                focusManager.moveFocus(FocusDirection.Next)
            },
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(
                    text = { Text(itemLabels[index]) },
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        focusRequester.freeFocus()
                        focusManager.moveFocus(FocusDirection.Next)
                        valueSelected(s)
                    },
                )
            }
        }
    }
}