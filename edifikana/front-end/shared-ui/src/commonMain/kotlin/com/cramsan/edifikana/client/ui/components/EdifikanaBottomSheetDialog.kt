package com.cramsan.edifikana.client.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import com.cramsan.framework.core.compose.Dialog

/**
 * An abstract [Dialog] that wraps its content in an [EdifikanaBottomSheet].
 *
 * Subclasses only need to implement [BottomSheetContent] with the desired sheet body.
 * Visibility is driven by the dialog's own [state], so callers interact exclusively
 * through [com.cramsan.framework.core.compose.DialogController.showDialog] and
 * [com.cramsan.framework.core.compose.DialogController.hideDialog].
 *
 * Calling [hide] from any context — including subclass composables — will animate the sheet
 * closed before removing it from the dialog stack.
 */
abstract class EdifikanaBottomSheetDialog : Dialog() {

    private val shouldHide = mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

        LaunchedEffect(shouldHide.value) {
            if (shouldHide.value) {
                sheetState.hide()
                shouldHide.value = false
                super@EdifikanaBottomSheetDialog.hide()
            }
        }

        EdifikanaBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { super@EdifikanaBottomSheetDialog.hide() },
        ) {
            BottomSheetContent()
        }
    }

    /**
     * Animates the sheet closed and then hides the dialog. Safe to call from any context;
     * falls back to an immediate hide if called before the sheet is composed.
     */
    override fun hide() {
        if (state.currentState || state.targetState) {
            shouldHide.value = true
        } else {
            super.hide()
        }
    }

    /**
     * The content to display inside the bottom sheet. Call [hide] to dismiss.
     */
    @Composable
    abstract fun BottomSheetContent()

}
