package com.cramsan.framework.core.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * A controller for managing dialogs in a Compose application. Uses the [Dialog] class to
 * create and manage dialogs.
 */
class DialogController {
    private val _dialogs = mutableStateListOf<Dialog>()
    val dialogs: SnapshotStateList<Dialog>
        get() = _dialogs

    /**
     * Shows a dialog. If the dialog is already shown, it will not be shown again.
     */
    fun showDialog(dialog: Dialog) {
        if (dialogs.contains(dialog)) {
            return
        }

        _dialogs.add(dialog)
        dialog.show()
    }

    /**
     * Hides a dialog. If the dialog is not shown, it will not be hidden.
     */
    fun hideDialog(dialog: Dialog) {
        if (!dialogs.contains(dialog)) {
            return
        }

        dialog.hide()
    }

    /**
     * Renders the currently displayed dialogs.
     */
    @Composable
    fun Render() {
        _dialogs.forEach { dialog ->
            val transitionState = dialog.state
            AnimatedVisibility(
                visibleState = transitionState,
            ) {
                dialog.Content()
            }
            val animationStarted by remember { dialog.animationStarted }
            val isAnimationIdle = transitionState.isIdle
            val isDialogShown = transitionState.currentState
            LaunchedEffect(animationStarted, isAnimationIdle, isDialogShown) {
                if (animationStarted && isAnimationIdle && !isDialogShown) {
                    _dialogs.remove(dialog)
                }
            }
        }
    }
}

/**
 * Creates a [DialogController] instance and remembers it across recompositions.
 *
 * @return A [DialogController] instance.
 */
@Composable
fun rememberDialogController(): DialogController {
    val controller = remember { DialogController() }
    return controller
}

/**
 * An abstract class representing a dialog. It manages its own visibility state and animation
 * state.
 *
 * @param state The initial visibility state of the dialog.
 */
abstract class Dialog(
    val state: MutableTransitionState<Boolean> = MutableTransitionState(false),
) {
    private val _animationStarted = mutableStateOf(false)

    /**
     * A state that indicates whether the animation to show this Dialog has started.
     */
    val animationStarted: State<Boolean>
        get() = _animationStarted

    /**
     * The content of the dialog. This function should be overridden to provide the
     * specific content of the dialog.
     */
    @Composable
    abstract fun Content()

    /**
     * Shows the dialog. This function should be called to display the dialog.
     */
    fun show() {
        state.targetState = true
        _animationStarted.value = true
    }

    /**
     * Hides the dialog. This function should be called to dismiss the dialog.
     */
    fun hide() {
        state.targetState = false
    }
}
