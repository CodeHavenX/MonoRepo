package com.cramsan.sample.mpplib.compose.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import jbcompose_mpplib.Res
import jbcompose_mpplib.compose
import org.jetbrains.compose.resources.painterResource

/**
 * Simple composable that shows a text and an animated visibility.
 */
@Composable
fun Content() {
    var visible by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier.clickable {
            visible = !visible
        }
    ) {
        Text("Hello World!")
        AnimatedVisibility(visible) {
            Image(
                painter = painterResource(Res.drawable.compose),
                contentDescription = null
            )
        }
    }
}
