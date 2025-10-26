package com.cramsan.ui.components.flippable

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import com.cramsan.ui.components.flippable.FlipAnimationType.HORIZONTAL_ANTI_CLOCKWISE
import com.cramsan.ui.components.flippable.FlipAnimationType.HORIZONTAL_CLOCKWISE
import com.cramsan.ui.components.flippable.FlipAnimationType.VERTICAL_ANTI_CLOCKWISE
import com.cramsan.ui.components.flippable.FlipAnimationType.VERTICAL_CLOCKWISE
import com.cramsan.ui.components.flippable.FlippableState.BACK
import com.cramsan.ui.components.flippable.FlippableState.FRONT
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * An Enum class to keep the state of side of [Flippable] like [FRONT] or [BACK]
 */
enum class FlippableState {
    INITIALIZED,
    FRONT,
    BACK
}

/**
 * An Enum class for animation type of [Flippable]. It has these 4 states:
 * [HORIZONTAL_CLOCKWISE], [HORIZONTAL_ANTI_CLOCKWISE], [VERTICAL_CLOCKWISE], and [VERTICAL_ANTI_CLOCKWISE]
 */
enum class FlipAnimationType {
    /**
     * Rotates the [Flippable] horizontally in the clockwise direction
     */
    HORIZONTAL_CLOCKWISE,

    /**
     * Rotates the [Flippable] horizontally in the anti-clockwise direction
     */
    HORIZONTAL_ANTI_CLOCKWISE,

    /**
     * Rotates the [Flippable] vertically in the clockwise direction
     */
    VERTICAL_CLOCKWISE,

    /**
     * Rotates the [Flippable] vertically in the anti-clockwise direction
     */
    VERTICAL_ANTI_CLOCKWISE
}

/**
 *  A composable which creates a card-like flip view for [frontSide] and [backSide] composables.
 *
 *  Example usage:
 *
 *  ```
 *  Flippable(
 *      frontSide = {
 *          // Composable content
 *      },
 *      backSide = {
 *          // Composable content
 *      }),
 *      flipController = rememberFlipController(),
 *      // ... other optional parameters
 *  ```
 *
 *  @param frontSide [Composable] method to draw any view for the front side
 *  @param backSide [Composable] method to draw any view for the back side
 *  @param flipController A [FlippableController] which lets you control flipping programmatically.
 *  @param modifier The Modifier for this [Flippable]
 *  @param contentAlignment The [Flippable] is contained in a [Box], so this tells the alignment to organize both Front and Back side composable.
 *  @param flipDurationMs The duration in Milliseconds for the flipping animation
 *  @param flipOnTouch If true, flipping will be done through clicking the Front/Back sides.
 *  @param flipEnabled Enable/Disable the Flipping animation.
 *  @param autoFlip If true, the [Flippable] will automatically flip back after [autoFlipDurationMs].
 *  @param autoFlipDurationMs The duration in Milliseconds to auto-flip back
 *  @param flipAnimationType The animation type of flipping effect.
 *  @param onFlippedListener The listener which is triggered when flipping animation is finished.
 *
 *  @author Wajahat Karim (https://wajahatkarim.com)
 */
@Suppress("MagicNumber")
@Composable
fun Flippable(
    frontSide: @Composable () -> Unit,
    backSide: @Composable () -> Unit,
    flipController: FlippableController,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    flipDurationMs: Int = 400,
    flipOnTouch: Boolean = true,
    flipEnabled: Boolean = true,
    autoFlip: Boolean = false,
    autoFlipDurationMs: Int = 1000,
    flipAnimationType: FlipAnimationType = FlipAnimationType.HORIZONTAL_CLOCKWISE,
    onFlippedListener: (currentSide: FlippableState) -> Unit = { _, -> }
) {
    var prevViewState by remember { mutableStateOf(FlippableState.INITIALIZED) }
    var flippableState by remember { mutableStateOf(FlippableState.INITIALIZED) }
    val transition: Transition<FlippableState> = updateTransition(
        targetState = flippableState,
        label = "Flip Transition",
    )
    flipController.setConfig(
        flipEnabled = flipEnabled
    )

    LaunchedEffect(key1 = flipController, block = {
        flipController.flipRequests
            .onEach {
                prevViewState = flippableState
                flippableState = it
            }
            .launchIn(this)
    })

    val flipCall: () -> Unit = {
        if (transition.isRunning.not() && flipEnabled) {
            prevViewState = flippableState
            if (flippableState == FRONT) {
                flipController.flipToBack()
            } else {
                flipController.flipToFront()
            }
        }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = transition.currentState, block = {
        if (transition.currentState == FlippableState.INITIALIZED) {
            prevViewState = FlippableState.INITIALIZED
            flippableState = FRONT
            return@LaunchedEffect
        }

        if (prevViewState != FlippableState.INITIALIZED && transition.currentState == flippableState) {
            onFlippedListener.invoke(flippableState)

            if (autoFlip && flippableState != FRONT) {
                scope.launch {
                    delay(autoFlipDurationMs.toLong())
                    flipCall()
                }
            }
        }
    })

    val frontRotation: Float by transition.animateFloat(
        transitionSpec = {
            when {
                FRONT isTransitioningTo BACK -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        0f at 0
                        90f at flipDurationMs / 2
                        90f at flipDurationMs
                    }
                }

                BACK isTransitioningTo FRONT -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        90f at 0
                        90f at flipDurationMs / 2
                        0f at flipDurationMs
                    }
                }

                else -> snap()
            }
        },
        label = "Front Rotation"
    ) { state ->
        when (state) {
            FlippableState.INITIALIZED, FRONT -> 0f
            BACK -> 180f
        }
    }

    val backRotation: Float by transition.animateFloat(
        transitionSpec = {
            when {
                FRONT isTransitioningTo BACK -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        -90f at 0
                        -90f at flipDurationMs / 2
                        0f at flipDurationMs
                    }
                }

                BACK isTransitioningTo FRONT -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        0f at 0
                        -90f at flipDurationMs / 2
                        -90f at flipDurationMs
                    }
                }

                else -> snap()
            }
        },
        label = "Back Rotation"
    ) { state ->
        when (state) {
            FlippableState.INITIALIZED, FRONT -> 180f
            BACK -> 0f
        }
    }

    Box(
        modifier = modifier
            .clickable(
                enabled = flipOnTouch,
                onClick = {
                    flipCall()
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        contentAlignment = contentAlignment
    ) {
        if (backRotation < 90) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        cameraDistance = 30f
                        clip = false
                        when (flipAnimationType) {
                            HORIZONTAL_CLOCKWISE -> rotationY = backRotation
                            HORIZONTAL_ANTI_CLOCKWISE -> rotationY = -backRotation
                            VERTICAL_CLOCKWISE -> rotationX = backRotation
                            VERTICAL_ANTI_CLOCKWISE -> rotationX = -backRotation
                        }
                    }
                    .zIndex(1F - backRotation)
            ) {
                backSide()
            }
        }

        if (frontRotation < 90) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        cameraDistance = 30f
                        clip = false
                        when (flipAnimationType) {
                            HORIZONTAL_CLOCKWISE -> rotationY = frontRotation
                            HORIZONTAL_ANTI_CLOCKWISE -> rotationY = -frontRotation
                            VERTICAL_CLOCKWISE -> rotationX = frontRotation
                            VERTICAL_ANTI_CLOCKWISE -> rotationX = -frontRotation
                        }
                    }
                    .zIndex(1F - frontRotation)
            ) {
                frontSide()
            }
        }
    }
}
