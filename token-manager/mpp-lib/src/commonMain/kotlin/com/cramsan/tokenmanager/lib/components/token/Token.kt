package com.cramsan.tokenmanager.lib.components.token

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.cramsan.tokenmanager.lib.state.TokenModel
import kotlin.math.roundToInt

@Composable
fun Token(
    tokenModel: TokenModel,
    expand: Boolean = false,
    onCloneSelected: (TokenModel) -> Unit = {},
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isSelected by remember { mutableStateOf(false) }
    val animatedScale: Float by animateFloatAsState(if (isSelected) 1f else 1f, label = "scale")
    var expand by remember(expand) { mutableStateOf(expand) }

    val shape = if (expand) {
        RoundedCornerShape(20.dp)
    } else {
        CircleShape
    }

    val size = if (expand) {
        200.dp
    } else {
        50.dp
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        Box(
            modifier = Modifier
                .scale(animatedScale)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        isSelected = true

                        do {
                            // This PointerEvent contains details including
                            // event, id, position and more
                            val event: PointerEvent = awaitPointerEvent()
                            // ACTION_MOVE loop

                            // Dont consume any event. If you do it would prevents other gestures or scroll to intercept
                            event.changes.forEach { pointerInputChange: PointerInputChange ->
                                // If we were to want to consume the events we could do
                                // pointerInputChange.consumePositionChange()
                            }
                        } while (event.changes.any { it.pressed })

                        // ACTION_UP is here
                        isSelected = false
                    }
                }
                .clickable {
                    expand = !expand
                }
                .shadow(10.dp, shape)
                .background(MaterialTheme.colorScheme.primary, shape)
                .size(size),
            contentAlignment = Alignment.Center,
        ) {
            if (expand) {
                ExpandedToken(
                    tokenModel,
                    onIncrementPower = { tokenModel.changePower(1) },
                    onDecrementPower = { tokenModel.changePower(-1) },
                    onIncrementToughness = { tokenModel.changeToughness(1) },
                    onDecrementToughness = { tokenModel.changeToughness(-1) },
                    onCloneSelected = onCloneSelected,
                )
            } else {
                CollapsedToken(tokenModel)
            }
        }
    }
}

@Composable
fun CollapsedToken(tokenModel: TokenModel) {
    Column {
        Text(tokenModel.name.value)
        Text("${ tokenModel.power.value }/${tokenModel.toughness.value}")
    }
}

@Composable
fun ExpandedToken(
    tokenModel: TokenModel,
    onIncrementPower: () -> Unit,
    onDecrementPower: () -> Unit,
    onIncrementToughness: () -> Unit,
    onDecrementToughness: () -> Unit,
    onCloneSelected: (TokenModel) -> Unit,
) {
    Column {
        IconButton(onClick = {
            onCloneSelected(tokenModel)
        }) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = null,
            )
        }
        Text(tokenModel.name.value)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onIncrementPower) {
                    Text("+")
                }
                Text("${tokenModel.power.value}")
                Button(onDecrementPower) {
                    Text("-")
                }
            }
            Text("/")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onIncrementToughness) {
                    Text("+")
                }
                Text("${tokenModel.toughness.value}")
                Button(onDecrementToughness) {
                    Text("-")
                }
            }
        }
    }
}
