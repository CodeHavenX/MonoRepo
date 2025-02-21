package ${PACKAGE_NAME}

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the ${NAME} feature screen.
 */
@Preview
@Composable
private fun ${NAME}ScreenPreview() {
    ${NAME}Content(
        content = ${NAME}UIState(true),
    )
}