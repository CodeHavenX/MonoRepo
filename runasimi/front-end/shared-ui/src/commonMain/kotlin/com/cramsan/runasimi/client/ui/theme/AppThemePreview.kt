package com.cramsan.runasimi.client.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cramsan.framework.core.compose.ui.ColorPreviewer
import com.cramsan.framework.core.compose.ui.ShapePreviewer
import com.cramsan.framework.core.compose.ui.TypographyPreviewer
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ThemeColorPreviewer() {
    AppTheme {
        ColorPreviewer(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun ThemeShapePreviewer() {
    AppTheme {
        ShapePreviewer(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun ThemeTypographyPreviewer() {
    AppTheme {
        TypographyPreviewer(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        )
    }
}
