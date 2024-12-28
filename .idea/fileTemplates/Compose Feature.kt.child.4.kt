package ${PACKAGE_NAME}

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

// TODO: Move this file to the desktop source set. Once done, remove this line
/**
 * Preview for the ${NAME} feature screen.
 */
@Preview
@Composable
private fun ${NAME}ScreenPreview() {
    ${NAME}Content(
        content = ${NAME}UIModel(""),
        loading = false,
    )
}