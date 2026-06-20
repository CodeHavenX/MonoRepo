package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Search input field with a leading search icon, styled consistently across FlyerBoard screens. */
@Composable
fun FlyerBoardSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(SEARCH_BAR_CORNER_PERCENT),
        modifier = modifier,
    )
}

private const val SEARCH_BAR_CORNER_PERCENT = 50
