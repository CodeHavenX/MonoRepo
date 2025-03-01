package ${PACKAGE_NAME}

import androidx.compose.runtime.Composable
import androidx.compose.desktop.ui.tooling.preview.Preview

/**
 * Preview for the ${NAME} feature screen.
 * TODO: Move this file to the JVM target, since the common target does not support previews.
 */
@Preview
@Composable
private fun ${NAME}ScreenPreview() {
    ${NAME}Content(
        content = ${NAME}UIState(true),
    )
}