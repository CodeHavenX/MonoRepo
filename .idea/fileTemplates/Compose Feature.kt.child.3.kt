package ${PACKAGE_NAME}

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

// TODO: Move this file to the desktop source set. Once done, remove this line
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