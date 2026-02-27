package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cramsan.ui.theme.Size

/**
 * Bottom sheet component that slides up from the bottom of the screen.
 *
 * On mobile (width < 600dp), fills the screen width.
 * On tablet/web (width >= 600dp), constrains content to max width and centers horizontally.
 *
 * @param visible Whether the bottom sheet is visible
 * @param onDismissRequest Callback when the sheet is dismissed
 * @param modifier Modifier for the bottom sheet
 * @param sheetState Optional sheet state for controlling the sheet programmatically
 * @param content Content to display in the sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdifikanaBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable () -> Unit,
) {
    if (visible) {
        BoxWithConstraints {
            val isMobile = maxWidth < 600.dp
            val screenMaxHeight = maxHeight

            ModalBottomSheet(
                onDismissRequest = onDismissRequest,
                sheetState = sheetState,
                modifier = modifier,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                // Wrap content in Box to apply width/height constraints on larger screens
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Column(
                        modifier = if (isMobile) {
                            Modifier
                                .fillMaxWidth()
                                .heightIn(max = screenMaxHeight)
                        } else {
                            Modifier
                                .widthIn(max = Size.COLUMN_MAX_WIDTH)
                                .heightIn(max = 600.dp)
                        }
                    ) {
                        content()
                    }
                }
            }
        }
    }
}
