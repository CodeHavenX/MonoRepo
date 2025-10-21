package ${PACKAGE_NAME}.${Package_Name}

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the ${Feature_Name} feature screen.
 */
@Preview
@Composable
private fun ${Feature_Name}ScreenPreview() {
    AppTheme {
        ${Feature_Name}Content(
            content = ${Feature_Name}UIState(
                title = "${Feature_Name}ScreenPreview",
                isLoading = true,
            ),
            onBackSelected = {},
        )
    }
}